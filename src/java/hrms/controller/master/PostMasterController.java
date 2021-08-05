/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.master;

import hrms.dao.master.DepartmentDAO;
import hrms.dao.master.PostDAO;
import hrms.model.master.GPost;
import hrms.model.master.Office;
import java.util.List;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Manas Jena
 */
@Controller
@SessionAttributes("LoginUserBean")
public class PostMasterController {
    
    @Autowired
    public PostDAO postDAO;
    @Autowired
    DepartmentDAO departmentDao;
    
    @ResponseBody
    @RequestMapping(value = "getDeptWisePostListJSON")
    public String GetDeptWisePostListJSON(@RequestParam("deptCode") String deptCode) {
        JSONArray json = null;
        try {
            List postlist = postDAO.getPostList(deptCode);
            json = new JSONArray(postlist);
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return json.toString();
    }
    
    @RequestMapping(value = "postList")
    public ModelAndView postList(@ModelAttribute("post") GPost post) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("post", post);
        mv.addObject("departmentList", departmentDao.getDepartmentList());
        mv.addObject("postList", postDAO.getPostList(post.getDeptcode()));
        mv.setViewName("/master/PostList");
        return mv;
    }
    
    @RequestMapping(value = "newPost")
    public ModelAndView newPost() {
        String path = "/master/PostEdit";
        ModelAndView mv = new ModelAndView(path, "post", new GPost());
        mv.addObject("departmentList", departmentDao.getDepartmentList());
        return mv;
    }
    @RequestMapping(value = "savePost")
    public ModelAndView savePost(@ModelAttribute("post") GPost post) {
        ModelAndView mv = new ModelAndView();
        String path = "/master/PostList";        
        postDAO.savePost(post);
        mv.addObject("departmentList", departmentDao.getDepartmentList());
        mv.addObject("postList", postDAO.getPostList(post.getDeptcode())); 
        mv.setViewName(path);
        return mv;
    }
    @RequestMapping(value = "getPostDetail")
    public ModelAndView getPostDetail(@RequestParam("postcode") String postcode){
        String path = "/master/PostEdit";
        ModelAndView mv = new ModelAndView();
        mv.addObject("post", postDAO.getPostDetail(postcode));
        mv.addObject("departmentList", departmentDao.getDepartmentList());
        mv.setViewName(path);
        return mv;
    }
}
