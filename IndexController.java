package jp.co.internous.team2409.controller;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.internous.team2409.model.domain.MstCategory;
import jp.co.internous.team2409.model.domain.MstProduct;
import jp.co.internous.team2409.model.domain.MstUser;
import jp.co.internous.team2409.model.form.SearchForm;
import jp.co.internous.team2409.model.mapper.MstCategoryMapper;
import jp.co.internous.team2409.model.mapper.MstProductMapper;
import jp.co.internous.team2409.model.mapper.MstUserMapper;
import jp.co.internous.team2409.model.session.LoginSession;

/**
 * 商品検索に関する処理を行うコントローラー
 * @author インターノウス
 *
 */
@Controller
@RequestMapping("/team2409")
public class IndexController {
	
	/*
	 * フィールド定義
	 */
	
	@Autowired
	private LoginSession loginSession;
	
	@Autowired
	private MstUserMapper userMapper;
	
	@Autowired
	private MstCategoryMapper categoryMapper;
	
	@Autowired
	private MstProductMapper productMapper;
	
	/**
	 * トップページを初期表示する。
	 * @param m 画面表示用オブジェクト
	 * @return トップページ
	 */
	@RequestMapping("/")
	public String index(Model m) {
		MstUser user = userMapper.findByUserNameAndPassword(loginSession.getUserName(), loginSession.getPassword());
		
		if(user == null) {
			loginSession.setLogined(false);
		} else {
			loginSession.setLogined(true);
		}
		
		if(loginSession.isLogined() == false && loginSession.getTmpUserId() == 0) {
			Random r = new Random();
			int tmpUserId = r.nextInt(900000000) + 100000000;
			loginSession.setTmpUserId(-tmpUserId);
		}
		
		m.addAttribute("loginSession", loginSession);
		
		List<MstCategory> categories = categoryMapper.find();
		m.addAttribute("categories", categories);
		
		List<MstProduct> products = productMapper.find();
		m.addAttribute("products", products);
		
		return "index";
	}
	
	/**
	 * 検索処理を行う
	 * @param f 検索用フォーム
	 * @param m 画面表示用オブジェクト
	 * @return トップページ
	 */
	@RequestMapping("/searchItem")
	public String searchItem(SearchForm f, Model m) {
		m.addAttribute("loginSession", loginSession);
		
		List<MstCategory> categories = categoryMapper.find();
		m.addAttribute("categories", categories);
		
		m.addAttribute("selected", f.getCategory());
		m.addAttribute("keywords", f.getKeywords());
		
		if(f.getCategory() == 0 && f.getKeywords().isEmpty() == true) {
			List<MstProduct> products = productMapper.find();
			m.addAttribute("products", products);
		}
		
		String str = f.getKeywords().replaceAll("　", " ").replaceAll(" +", " ").trim();
		String[] keywords = str.split(" ");
		
		if(f.getCategory() == 0 && f.getKeywords().isEmpty() == false) {
			List<MstProduct> products = productMapper.findByProductName(keywords);
			m.addAttribute("products", products);
		}
		
		if(f.getCategory() > 0) {
			List<MstProduct> products = productMapper.findByCategoryAndProductName(f.getCategory(), keywords);
			m.addAttribute("products", products);
		}
		
		return "index";
	}
}
