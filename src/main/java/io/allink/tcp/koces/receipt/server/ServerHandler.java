package io.allink.tcp.koces.receipt.server;

import java.util.Objects;

import org.springframework.stereotype.Component;

import io.allink.tcp.koces.receipt.model.Store;
import io.allink.tcp.koces.receipt.protocol.KocesMessage;
import io.allink.tcp.koces.receipt.service.MerchantReceiptService;
import io.allink.tcp.koces.receipt.service.StoreService;
import io.allink.tcp.koces.receipt.util.JsonUtil;
import io.allink.tcp.koces.receipt.util.PayloadNormalizer;
import io.allink.tcp.koces.receipt.util.StringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static io.allink.tcp.koces.receipt.common.Code.CANCEL_TYPE_MAP;
import static io.allink.tcp.koces.receipt.common.Code.CARD_COMPANY_MAP;
import static io.allink.tcp.koces.receipt.common.Code.CHECK_YN_MAP;
import static io.allink.tcp.koces.receipt.common.Code.DDC_YN_MAP;
import static io.allink.tcp.koces.receipt.common.Code.FOREIGN_YN_MAP;
import static io.allink.tcp.koces.receipt.common.Code.PAY_TYPE_MAP;
import static io.allink.tcp.koces.receipt.common.Code.SVC_TYPE_MAP;
import static io.allink.tcp.koces.receipt.common.Code.SWIPE_MAP;
import static io.allink.tcp.koces.receipt.common.Code.TRD_TYPE_MAP;

@Slf4j
@Component
@ChannelHandler.Sharable
@RequiredArgsConstructor
public class ServerHandler extends ChannelInboundHandlerAdapter {

  private static final AttributeKey<KocesMessage> RECEIPT_KEY = AttributeKey.valueOf("koces.receipt");

  private final StoreService storeService;

  private final MerchantReceiptService mertReceiptService;

  @Override
  public void handlerAdded(ChannelHandlerContext ctx) {
    int DATA_LENGTH = 600;
    ctx.alloc().buffer(DATA_LENGTH);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    String remoteAddress = ctx.channel().remoteAddress().toString();
    log.info("channel active: {}", remoteAddress);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    String serverId = ctx.channel().id().asShortText();
    log.info("Client disconnected: " + ctx.channel().remoteAddress());
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object message) {
    KocesMessage receipt = (KocesMessage) message;
    ctx.channel().attr(RECEIPT_KEY).set(receipt);
    log.info("Received message: {}", receipt);
    Store store = storeService.findAllByBusinessNoAndDeviceId(receipt.getBusinessNo(), receipt.getTermId());

    // 미등록이어도 받아서 쌓아둔다.
    //   버리면 뒤늦게 상점·단말을 등록해도 그 사이 영수증을 되살릴 방법이 없다.
    //   거부 응답을 주면 VAN사 쪽에 오류로 쌓이고 재전송을 유발하기도 한다.
    //   status로 표시해 두고 어드민에서 사후 등록으로 처리한다.
    String status = null;
    if (store == null) {
      status = "NO_STORE";
      log.warn("미등록 가맹점 수신 - 적재 후 사후 등록 대상 : businessNo = {} terminalId = {}",
          receipt.getBusinessNo(), receipt.getTermId());
    } else if (mertReceiptService.isNotExistsMerchantTag(receipt.getTermId())) {
      status = "NO_TAG";
      log.warn("태그 없음 - 적재 후 사후 등록 대상 : merchantNo = {} terminalId = {}",
          receipt.getMchNo(), receipt.getTermId());
    }

    if (mertReceiptService.isExists((receipt.getTransDate() + "-" + receipt.getTrdUniKey()).trim())) {
      log.info("중복 전문 수신 trxId: {}", (receipt.getTransDate() + "-" + receipt.getTrdUniKey()).trim());
      receipt.setAnswerCd("ER01"); //중복 요청
    } else {
      // 성공 응답
      receipt.setAnswerCd("0000");
      String svcType = Objects.toString(receipt.getSvcType(), "");
      KocesMessage kocesMessage = new KocesMessage(receipt);

      kocesMessage.setTrdType(TRD_TYPE_MAP.getOrDefault(svcType + receipt.getTrdType(), svcType + receipt.getTrdType()));
      kocesMessage.setCardNo(StringUtil.maskCardNumber(receipt.getCardNo()));
      kocesMessage.setSvcType(SVC_TYPE_MAP.getOrDefault(svcType, svcType));
      kocesMessage.setPayGubun(PAY_TYPE_MAP.getOrDefault(receipt.getPayGubun(), receipt.getPayGubun()));
      kocesMessage.setInsMon(receipt.getInsMon().equals("00") ? "일시불" : receipt.getInsMon());
      kocesMessage.setCancelCd(CANCEL_TYPE_MAP.getOrDefault(receipt.getCancelCd(), receipt.getCancelCd()));
      kocesMessage.setIssCd(CARD_COMPANY_MAP.getOrDefault(receipt.getIssCd(), receipt.getIssCd()));
      kocesMessage.setBuyCd(CARD_COMPANY_MAP.getOrDefault(receipt.getBuyCd(), receipt.getBuyCd()));
      kocesMessage.setDdcYn(DDC_YN_MAP.getOrDefault(svcType + receipt.getDdcYn(), svcType + receipt.getDdcYn()));
      kocesMessage.setCheckYn(CHECK_YN_MAP.getOrDefault(svcType + receipt.getCheckYn(), svcType + receipt.getCheckYn()));
      kocesMessage.setForeignYn(FOREIGN_YN_MAP.getOrDefault(svcType + receipt.getForeignYn(), svcType + receipt.getForeignYn()));
      kocesMessage.setSwipe(SWIPE_MAP.getOrDefault(receipt.getSwipe(), receipt.getSwipe()));
      // mchNo는 KOCES 원본값 그대로 저장 (store_uid로 덮어쓰지 않음)
      String payloadJson    = JsonUtil.toJson(store, kocesMessage);
      String normalizedJson = PayloadNormalizer.toNormalizedJson(store, kocesMessage);
      mertReceiptService.insertWithJson(receipt, payloadJson, normalizedJson, status);
    }
    // 응답 발송
    ByteBuf reqBuf = Unpooled.copiedBuffer(receipt.getResponse(), CharsetUtil.UTF_8);
    log.info("Server response: {}", receipt.getResponse());
    ctx.writeAndFlush(reqBuf).addListener(ChannelFutureListener.CLOSE);

  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    // 실패 응답
    KocesMessage receipt = ctx.channel().attr(RECEIPT_KEY).get();
    if (receipt != null) {
      receipt.setAnswerCd("ER03");
      ByteBuf reqBuf = Unpooled.copiedBuffer(receipt.getResponse(), CharsetUtil.UTF_8);
      ctx.writeAndFlush(reqBuf).addListener(ChannelFutureListener.CLOSE);
    }

    // Close the connection when an exception is raised.
    ctx.close();
    log.error("exception caught: {}", cause.getMessage());
  }
}
