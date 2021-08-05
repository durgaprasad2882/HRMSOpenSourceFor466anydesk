/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.upload;

import hrms.model.login.LoginUserBean;
import hrms.model.upload.UploadedFile;
import hrms.validator.UploadFileValidator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Surendra
 */
@Controller
public class UploadController implements ServletContextAware{

    //@Autowired
    //UploadFileValidator fileValidator;
    
     private ServletContext servletContext;

     
     @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    

    @RequestMapping("/fileUploadForm")
    public ModelAndView getUploadForm(
            @ModelAttribute("uploadedFile") UploadedFile uploadedFile,
            BindingResult result) {
        return new ModelAndView("tab/PhotoUploadInterface");
    }

    @RequestMapping("/fileUpload")
    public ModelAndView fileUploaded(
            @ModelAttribute("uploadedFile") UploadedFile uploadedFile,
            BindingResult result,HttpSession sessionObj) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        LoginUserBean lub = null;
        MultipartFile file = uploadedFile.getFile();
        //fileValidator.validate(uploadedFile, result);
        if (sessionObj.getAttribute("LoginUserBean") != null) {
            lub = (LoginUserBean) sessionObj.getAttribute("LoginUserBean");
        }
        String fileName = lub.getEmpid()+".jpg";
        String filepath=servletContext.getInitParameter("PhotoPath");
        if (result.hasErrors()) {
            return new ModelAndView("tab/PhotoUploadInterface");
        }

        try {
            inputStream = file.getInputStream();

            File newFile = new File(filepath + fileName);
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            outputStream = new FileOutputStream(newFile);
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
        }

        return new ModelAndView("tab/PhotoUploadInterface");
    }

    
}
