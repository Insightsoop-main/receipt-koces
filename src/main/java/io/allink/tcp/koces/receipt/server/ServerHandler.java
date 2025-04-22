package io.allink.tcp.koces.receipt.server;

import java.util.Objects;

import org.springframework.stereotype.Component;

import io.allink.tcp.koces.receipt.model.Store;
import io.allink.tcp.koces.receipt.protocol.KocesMessage;
import io.allink.tcp.koces.receipt.service.MerchantReceiptService;
import io.allink.tcp.koces.receipt.service.StoreService;
import io.allink.tcp.koces.receipt.util.JsonUtil;
import io.allink.tcp.koces.receipt.util.StringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
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

  private KocesMessage receipt;

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
    receipt = (KocesMessage) message;
    log.info("Received message: {}", receipt);
    Store store;
    if ("CB".equals(receipt.getSvcType())) { //현금영수증
      store = storeService.findAllByBusinessNoAndDeviceId(receipt.getBusinessNo(), receipt.getTermId());
    } else {
      store = storeService.getStore(receipt.getMchNo(), receipt.getTermId());
    }

    if (store == null) {
      receipt.setAnswerCd("ER02"); //가맹점 없음
      log.error("store not found: merchantNo = {} termialId = {}", receipt.getMchNo(), receipt.getTermId());
    } else if (mertReceiptService.isNotExistsMerchantTag( receipt.getTermId())) {
      log.error("tag not found: merchantNo = {} termialId = {}", receipt.getMchNo(), receipt.getTermId());
      receipt.setAnswerCd("ER02"); //등록된 태그가 없음
    } else if (mertReceiptService.isExists(receipt.getTrdUniKey())) {
      receipt.setAnswerCd("ER01"); //중복 요청
    } else {
      // ■ 성공 응답
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

      mertReceiptService.insertWithJson(receipt, JsonUtil.toJson(store, kocesMessage));
    }
    // ■ 응답 발송
    ByteBuf reqBuf = Unpooled.copiedBuffer(receipt.getResponse(), CharsetUtil.UTF_8);
    ctx.writeAndFlush(reqBuf).addListener(ChannelFutureListener.CLOSE);

  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    // 실패 응답
    receipt.setAnswerCd("ER03");
    ByteBuf reqBuf = Unpooled.copiedBuffer(receipt.getResponse(), CharsetUtil.UTF_8);
    ctx.writeAndFlush(reqBuf).addListener(ChannelFutureListener.CLOSE);

    // Close the connection when an exception is raised.
    ctx.close();
    log.error("exception caught: {}", cause.getMessage());
  }
}
