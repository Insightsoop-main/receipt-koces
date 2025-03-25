package io.allink.tcp.koces.receipt.server;

import io.allink.tcp.koces.receipt.model.ReceiptEntity;
import io.allink.tcp.koces.receipt.model.StoreEntity;
import io.allink.tcp.koces.receipt.protocol.pReceipt;
import io.allink.tcp.koces.receipt.service.CodeService;
import io.allink.tcp.koces.receipt.service.mertReceiptService;
import io.allink.tcp.koces.receipt.service.ReceiptService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@ChannelHandler.Sharable
@RequiredArgsConstructor
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private pReceipt receipt;

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private CodeService codeService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private mertReceiptService mertReceiptService;


    private final int DATA_LENGTH = 600;
    private ByteBuf buff;


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        buff = ctx.alloc().buffer(DATA_LENGTH);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        buff = null;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String remoteAddress = ctx.channel().remoteAddress().toString();
        log.info("channel active: {}", remoteAddress);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String serverId = ctx.channel().id().asShortText();
        log.info("Client disconnected: " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {

        receipt = (pReceipt) message;

        // ■ 성공 응답
        receipt.setAnswerCd("0000");
        receipt.getReceiptPrint();


        // ■ 통신 Data Database 저장
        ReceiptEntity receiptEntity = new ReceiptEntity();

        receiptEntity.setReceiptEntity(receipt);

        Long retReceiptSeq = receiptService.saveAndGetSerialNumber(receiptEntity);

        log.info("📩 retReceiptSeq: " + retReceiptSeq);


        // ■ Json 형태 로 Data 변경 (코드 성 -> 노출 데이터로 치환)
        ReceiptEntity jsonReceiptEntity = new ReceiptEntity();
        jsonReceiptEntity = receiptEntity;

        String cdType = jsonReceiptEntity.getSvcType();

        jsonReceiptEntity.setTrdType(Objects.toString(codeService.findByCdGrpAndCdTypeAndCdVal("trdType", cdType, receiptEntity.getTrdType()), ""));
        jsonReceiptEntity.setCardNo(StringUtil.maskCardNumber(receiptEntity.getCardNo()));
        jsonReceiptEntity.setPayGubun(Objects.toString(codeService.findByCdGrpAndCdTypeAndCdVal("payGubun", null, receiptEntity.getPayGubun()), ""));
        jsonReceiptEntity.setInsMon(receiptEntity.getInsMon().equals("00") ? "일시불" : receiptEntity.getInsMon());
        jsonReceiptEntity.setCancelCd(Objects.toString(codeService.findByCdGrpAndCdTypeAndCdVal("cancelCd", null, receiptEntity.getCancelCd()), ""));
        jsonReceiptEntity.setIssCd(Objects.toString(codeService.findByCdGrpAndCdTypeAndCdVal("credit", null, receiptEntity.getIssCd()), ""));
        jsonReceiptEntity.setBuyCd(Objects.toString(codeService.findByCdGrpAndCdTypeAndCdVal("credit", null, receiptEntity.getBuyCd()), ""));
        jsonReceiptEntity.setDdcYn(Objects.toString(codeService.findByCdGrpAndCdTypeAndCdVal("ddcYn", cdType, receiptEntity.getDdcYn()), ""));

        StoreEntity storeEntity = storeService.findTopByBusinessNoOrderByRegDateDesc(StringUtil.formatBusinessNo(receiptEntity.getBusinessNo()));

        JsonUtil jsonUtil = new JsonUtil();
        String jsonData = jsonUtil.toJson(storeEntity, jsonReceiptEntity);


        // ■ receipt_merchants_payloads Insert 처리
        mertReceiptService.insertWithJson(receiptEntity.getMchNo(), jsonData, receiptEntity.getTermId(), receiptEntity.getTrdUniKey());


        // ■ 응답 발송
        ByteBuf reqBuf = Unpooled.copiedBuffer(receipt.setReceipt(), CharsetUtil.UTF_8);
        ctx.writeAndFlush(reqBuf).addListener(ChannelFutureListener.CLOSE);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 실패 응답
        receipt.setAnswerCd("9999");

        ByteBuf reqBuf = Unpooled.copiedBuffer(receipt.setReceipt(), CharsetUtil.UTF_8);
        ctx.writeAndFlush(reqBuf).addListener(ChannelFutureListener.CLOSE);

        // Close the connection when an exception is raised.
        ctx.close();
        log.error("exception caught: {}", cause.getMessage());
    }
}
