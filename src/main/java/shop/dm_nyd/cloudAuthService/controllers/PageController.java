package shop.dm_nyd.cloudAuthService.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import shop.dm_nyd.cloudAuthService.service.SrvcService;
import shop.dm_nyd.cloudAuthService.vo.SrvcVo;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/pg")
@RequiredArgsConstructor
public class PageController {

	@Autowired
	private SrvcService srvcService;

	@GetMapping("/{one}")
    public String index(@PathVariable("one") String one, Model model) throws Exception{
		
		if("login".equals(one)) {
			SrvcVo param = new SrvcVo();
			List<SrvcVo> srvcList = srvcService.selectSrvcList(param); 
			System.out.println(srvcList);
			model.addAttribute("srvcList", srvcList);
		}
		
		
        return one;
    }
}
