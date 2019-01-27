package top.lemno.pay.commons.api;

import top.lemno.pay.commons.dto.UnifiedRequest;
import top.lemno.pay.commons.dto.UnifiedResponse;

/**
 * 统一三方支付接口
 * 
 * @author mux
 *
 */
public interface UnifiedT3dPayApi<C extends UnifiedRequest> {

  UnifiedResponse trade(C request);

  UnifiedResponse cancel(C request);

  UnifiedResponse query(C request);

}
