package com.ksyun.trade.controller.online;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksyun.trade.dto.RegionDTO;
import com.ksyun.trade.dto.TradeProductConfigDTO;
import com.ksyun.trade.dto.TradeResultDTO;
import com.ksyun.trade.dto.UserDTO;
import com.ksyun.trade.pojo.TradeOrder;
import com.ksyun.trade.pojo.TradeProductConfig;
import com.ksyun.trade.rest.RestResult;
import com.ksyun.trade.service.TradeOrderService;
import com.ksyun.trade.service.TradeProductConfigService;
import com.ksyun.trade.util.PinyinUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

@RestController
@RequestMapping(value = "/online", produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
public class OrderInfoController {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private TradeOrderService tradeOrderService;

    @Resource
    private TradeProductConfigService tradeProductConfigService;

    @RequestMapping("/queryOrderInfo")
    public ResponseEntity query(@RequestParam(value = "id") Integer id, HttpServletRequest request) throws IOException {

        //获取gateway传来的upstream参数
        String upstream = request.getParameter("upstream");


        TradeOrder tradeOrder = tradeOrderService.getById(id);
        BigDecimal priceValue = tradeOrder.getPriceValue();
        Integer userId = tradeOrder.getUserId();
        Integer regionId = tradeOrder.getRegionId();


        //构建RestTemplate
        //从第三方接口获取userResponseEntityBody中的data
        String targetUrl = "http://campus.meta.ksyun.com:8090/online/user/" + userId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> userResponseEntity = restTemplate.exchange(targetUrl, HttpMethod.GET, requestEntity, String.class);
        String userResponseEntityBody = userResponseEntity.getBody();
        System.out.println("userResponseEntityBody = " + userResponseEntityBody);


        //获得userDTO
        //把json字符串映射成对象
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = new ObjectMapper().readTree(userResponseEntityBody);
        UserDTO userDTO = objectMapper.convertValue(jsonNode.get("data"), UserDTO.class);
        System.out.println("userDTO = " + userDTO);


        //获得regionDTO
        //从第三方接口获取regionResponseEntityBody中的data
        targetUrl = "http://campus.meta.ksyun.com:8090/online/region/name/" + regionId;
        ResponseEntity<String> regionResponseEntity = restTemplate.exchange(targetUrl, HttpMethod.GET, requestEntity, String.class);
        String regionResponseEntityBody = regionResponseEntity.getBody();
//        System.out.println("regionResponseEntityBody = " + regionResponseEntityBody);
        JsonNode jsonNode1 = new ObjectMapper().readTree(regionResponseEntityBody);
        String regionDTOString = jsonNode1.get("data").asText();
//        System.out.println("regionDTOString = " + regionDTOString);
        //把汉字转换成拼音
        String pinYin = PinyinUtils.getPinYin(regionDTOString);
        RegionDTO regionDTO = new RegionDTO();
        regionDTO.setCode(pinYin);
        regionDTO.setName(regionDTOString);
        System.out.println("regionDTO = " + regionDTO);


        //获得List<TradeProductConfigDTO>集合
        TradeProductConfig tradeProductConfig = tradeProductConfigService.selectByOrderId(id);
        System.out.println("tradeProductConfig = " + tradeProductConfig);
        /**
         * 为了得到List<TradeProductConfigDTO>集合，先把tradeProductConfig映射成json字符串
         * 然后再把json字符串映射成TradeProductConfigDTO
         * 接着放到List中即可
         */
        ObjectMapper objectMapper1 = new ObjectMapper();
        String tradeProductConfigJson = objectMapper1.writeValueAsString(tradeProductConfig);
        System.out.println("tradeProductConfigJson = " + tradeProductConfigJson);
        TradeProductConfigDTO tradeProductConfigDTO = objectMapper1.readValue(tradeProductConfigJson, TradeProductConfigDTO.class);
        ArrayList<TradeProductConfigDTO> tradeProductConfigDTOList = new ArrayList<>();
        tradeProductConfigDTOList.add(tradeProductConfigDTO);


        //TradeResultDTO需要的属性都获取完毕，接下来封装成TradeResultDTO然后当作data包装进ResponseEntity返回结构
        TradeResultDTO tradeResultDTO = new TradeResultDTO();
        tradeResultDTO.setUpstream(upstream);
        tradeResultDTO.setId(id);
        tradeResultDTO.setPriceValue(priceValue);
        tradeResultDTO.setUser(userDTO);
        tradeResultDTO.setRegion(regionDTO);
        tradeResultDTO.setConfigs(tradeProductConfigDTOList);

        System.out.println("tradeResultDTO = " + tradeResultDTO);
        ObjectMapper objectMapper2 = new ObjectMapper();
        String tradeResultDTOJson = objectMapper2.writeValueAsString(tradeResultDTO);
        System.out.println("tradeResultDTOJson = " + tradeResultDTOJson);


        //这里把从数据库查的id,priceValue,configs和从第三方接口查的id,priceValue封装成TradeResultDTO
        // 再封装成ResponseEntity然后返回
        HttpStatus status = HttpStatus.OK;
//        ResponseEntity<TradeResultDTO> responseEntity = new ResponseEntity<>(tradeResultDTO, status);
        return ResponseEntity.ok(tradeResultDTO);
//        return responseEntity;
    }
}
