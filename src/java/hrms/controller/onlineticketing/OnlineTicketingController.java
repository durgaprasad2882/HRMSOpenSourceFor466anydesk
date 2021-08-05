/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.onlineticketing;

import hrms.common.DataBaseFunctions;
import hrms.dao.master.OnlineTicketTopicDAO;
import hrms.dao.onlineTicketing.OnlineTicketingDAO;
import hrms.model.login.LoginUserBean;
import hrms.model.onlineTicketing.OnlineTicketing;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import java.io.IOException;
import java.io.OutputStream;
import org.springframework.web.multipart.MultipartFile;

@Controller
@SessionAttributes("LoginUserBean")
public class OnlineTicketingController implements ServletContextAware {

    @Autowired
    OnlineTicketingDAO onlineTicketDAO;
    @Autowired
    OnlineTicketTopicDAO ticketTopicDAO;

    private ServletContext servletContext;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @RequestMapping(value = "onlineticket.htm", method = RequestMethod.GET)
    public ModelAndView onlineTicketData(@ModelAttribute("LoginUserBean") LoginUserBean lub, Map<String, Object> model) {
        // OnlineTicketing ot=new OnlineTicketing();
        ModelAndView mv =null;
        OnlineTicketing onlineticket = new OnlineTicketing();
        onlineticket.setUsername(lub.getUsername());
        System.out.println("==============" + lub.getUserid());
        System.out.println("==============" + lub.getEmpid());

        System.out.println("********************************" + onlineticket.getUsername());
         mv = new ModelAndView("/onlineticketing/OnlineTicketingData", "onlineticketing", onlineticket);

        List topiclist = ticketTopicDAO.getTicketTopicList();
        System.out.println("**size==============="+topiclist.size());
        mv.addObject("topiclist", topiclist);

        return mv;
    }

    @RequestMapping(value = "onlineticket.htm", params = "save")
    public String onlineTicketData(@ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("onlineticketing") OnlineTicketing onlineticket, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws IOException {

        Connection con = null;
        PreparedStatement pst = null;
        String fileName = "";
        InputStream inputStream = null;
        String filePath = "";
        OutputStream outputStream = null;
        //TicketAttachment tktAttach = null;
        try {
            filePath = servletContext.getInitParameter("FilePath");
            MultipartFile ticketfile = onlineticket.getFile();
            fileName = System.currentTimeMillis() + "";
            String dirpath = filePath + "\\";
            File newfile = new File(dirpath);
            if (!newfile.exists()) {
                newfile.mkdirs();
            }
            outputStream = new FileOutputStream(dirpath + fileName);
            int read = 0;
            byte[] bytes = new byte[1024];

            inputStream = ticketfile.getInputStream();
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            if (ticketfile.getOriginalFilename() != null && !ticketfile.getOriginalFilename().equals("")) {
                // tktAttach = new TicketAttachment();
                onlineticket.setDfileName(fileName);
                onlineticket.setOfileName(ticketfile.getOriginalFilename());
                onlineticket.setFileType(ticketfile.getContentType());
                onlineticket.setFilePath(dirpath);
                onlineticket.setRefType("T-Ticket");
                //ticketAttachDAO.addAttachDocumentInfo(onlineticket);
            }
            onlineticket.setUserId(lub.getUserid());

            onlineTicketDAO.addTicket(onlineticket);
            outputStream.close();
            outputStream.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
            DataBaseFunctions.closeSqlObjects(pst);
        }
        return "redirect:/onlineticketlist.htm";
    }

    @RequestMapping(value = "onlineticketlist.htm", method = RequestMethod.GET)
    public String onlineTicketList(@ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("onlineticketing") OnlineTicketing onlineticket, Map<String, Object> model) {

//        System.out.println("==============" + lub.getUserid());
//        System.out.println("==============" + lub.getEmpid());
//        System.out.println("*************++^^^++=*******************" + lub.getUsername());
        List onLineTicketList = onlineTicketDAO.getTicketList(lub.getUserid());
        model.put("onlineticketlist", onLineTicketList);
        return "onlineticketing/OnlineTicketingList";
    }

    @RequestMapping(value = "onlineticketlist.htm", method = RequestMethod.GET, params = "newticket")
    public String newTicket(@ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("onlineticketing") OnlineTicketing onlineticket, Map<String, Object> model) {

//        System.out.println("==============" + lub.getUserid());
//        System.out.println("==============" + lub.getEmpid());
//        System.out.println("*************++++=*******************" + lub.getUsername());
        onlineticket.setUsername(lub.getUsername());
        List onLineTicketList = onlineTicketDAO.getTicketList(lub.getUserid());
        model.put("onlineticketlist", onLineTicketList);
        return "redirect:/onlineticket.htm";
    }

    @RequestMapping(value = "onlineticket.htm", params = "cancel")
    public String onlineTicketList(@ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("onlineticketing") OnlineTicketing onlineticket, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws IOException {
        return "redirect:/onlineticketlist.htm";
    }

}
