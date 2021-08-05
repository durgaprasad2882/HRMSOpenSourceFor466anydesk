
package hrms.controller.loanworkflow;



import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import hrms.dao.loanworkflow.LoanApplyDAO;
import hrms.model.loanworkflow.LoanForm;
import hrms.model.loanworkflow.LoanGPFForm;
import hrms.model.loanworkflow.LoanHBAForm;
import hrms.model.loanworkflow.LoanTempGPFForm;
import hrms.model.login.LoginUserBean;
import java.util.Calendar;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author lenovo
 */
@Controller
@SessionAttributes("LoginUserBean")    
public class LoanController implements ServletContextAware {
    
    @Autowired
    LoanApplyDAO loanworkflowDao ;        
    
    private ServletContext context;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }
    
    @RequestMapping(value = "loanapplyController", method = {RequestMethod.POST,RequestMethod.GET})
    public String loanApply(@ModelAttribute("LoginUserBean") LoginUserBean lub,@ModelAttribute("LoanForm") LoanForm loanform, ModelMap model) {
        loanform=loanworkflowDao.displayEmpDetails(lub.getEmpid());
       // loanworkflowDao.saveLoanData(loanform);
        model.put("LoanForm", loanform);
        return "/loanworkflow/loanapply";
    }
    
    @RequestMapping(value = "saveLoan", method = {RequestMethod.POST,RequestMethod.GET},params="Save")
    public String saveLoan(@ModelAttribute("LoginUserBean") LoginUserBean lub,@ModelAttribute("LoanForm") LoanForm loanform) {
        String filepath = context.getInitParameter("LoanPath");
        loanworkflowDao.saveLoanData(loanform,lub.getEmpid(),filepath);
       // System.out.println("loan press save page"+lub.getEmpid());
        return "/loanworkflow/loanList";
    }
    
 /*   @RequestMapping(value = "saveLoan", method = {RequestMethod.POST,RequestMethod.GET},params="Back")
    public String getloanList(@ModelAttribute("LoginUserBean") LoginUserBean lub,@ModelAttribute("LoanForm") LoanForm loanform) {
        System.out.println("Back");
        return "/loanworkflow/loanList";
    }*/
    
    @RequestMapping(value = "ChangePostLoanController", method = RequestMethod.GET)
    public String ChangePost() {

        String path = "/loanworkflow/PostChange";
        return path;
    }     
    
    @RequestMapping(value = "loanList", method = RequestMethod.GET)
    public String LoanListPage(Model model,@ModelAttribute("LoginUserBean") LoginUserBean lub){
        //System.out.println("empid==="+lub.getEmpid());
        model.addAttribute("empid", lub.getEmpid());
        return "/loanworkflow/loanList";
    }
    
    @ResponseBody
    @RequestMapping(value = "GetLoanListJSON", method = {RequestMethod.GET, RequestMethod.POST})
    public String getLoanListJSON(@ModelAttribute("LoginUserBean") LoginUserBean lub){
        JSONArray json = null;
        try{
            
            //System.out.println("session for empid=="+lub.getEmpid());
            List loanlist = loanworkflowDao.getLoanList(lub.getEmpid());
            json = new JSONArray(loanlist);
        }catch(Exception e){
            e.printStackTrace();
        }
      return json.toString();
    }
    
    @RequestMapping(value = "loanApprove", method = RequestMethod.GET)
    public String LoanListPage(Model model,@ModelAttribute("LoginUserBean") LoginUserBean lub,@RequestParam("taskId") int taskid){
      LoanForm loan=new LoanForm();
      loan=loanworkflowDao.getLoanDetails(taskid,lub.getEmpid());
     
      model.addAttribute("LoanForm", loan);
        return "/loanworkflow/loanapprove";
    }
    
   @ResponseBody
    @RequestMapping(value = "getPostListLoanworkflowJSON", method = {RequestMethod.GET, RequestMethod.POST})
    public String getPostListLoanworkflowJSON(@RequestParam("offcode") String offcode){
        JSONArray json = null;
        try{
            List postlist = loanworkflowDao.getPostList(offcode);
            json = new JSONArray(postlist);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            
        }
      return json.toString();
    }
    @ResponseBody
    @RequestMapping(value = "getprocessList", method = {RequestMethod.GET, RequestMethod.POST})
    public String getprocessListJSON(@RequestParam("processid") String processid){
        JSONArray json = null;
        try{
            List postlist = loanworkflowDao.getprocessList(processid);
            json = new JSONArray(postlist);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            
        }
      return json.toString();
    }
    @RequestMapping(value = "saveApproveLoan", method = {RequestMethod.POST,RequestMethod.GET},params="Save")
    public String saveApproveLoan(@ModelAttribute("LoginUserBean") LoginUserBean lub,@ModelAttribute("LoanForm") LoanForm loanform) {
        
        loanworkflowDao.saveApproveLoanData(loanform,lub.getEmpid());
       // System.out.println("loan press save page"+lub.getEmpid());
        return "/loanworkflow/loanList";
    }
    @RequestMapping(value = "saveLoansaction", method = {RequestMethod.POST,RequestMethod.GET},params="Save")
    public ModelAndView saveLoansaction(@ModelAttribute("LoginUserBean") LoginUserBean lub,@ModelAttribute("LoanForm") LoanForm loanform) {
        
        loanworkflowDao.saveLoansaction(loanform);
       // System.out.println("loan press save page"+lub.getEmpid());
        //return "/loanworkflow/loanList";
        return new ModelAndView("redirect:/SactionOrder.htm?taskid="+loanform.getTaskid()+"&loanid="+loanform.getLoanId());
    }
    
   @RequestMapping(value = "ReapplyLoanAction", method = RequestMethod.GET)
    public String ReapplyLoanAction(Model model,@ModelAttribute("LoginUserBean") LoginUserBean lub,@RequestParam("opt") String option,@RequestParam("loanid") int loanid){
      LoanForm loan=new LoanForm();
      loan=loanworkflowDao.ReplyLoan(option,lub.getEmpid(),loanid);
     
      model.addAttribute("LoanForm", loan);
        return "/loanworkflow/loanreapply";
    }
    
    @RequestMapping(value = "SactionOrder", method = RequestMethod.GET)
    public String SactionOrder(Model model,@ModelAttribute("LoginUserBean") LoginUserBean lub,@RequestParam("taskid") int taskid,@RequestParam("loanid") int loanid){
      LoanForm loan=new LoanForm();
      loan=loanworkflowDao.SactionLoanOrder(taskid,lub.getEmpid(),loanid);
     
      model.addAttribute("LoanForm", loan);
       Calendar now = Calendar.getInstance();
      
       int cyear = now.get(Calendar.YEAR);
       int nextYear=cyear+1;
       String fyear=cyear+" - "+nextYear;
      model.addAttribute("curreyear", fyear);
        return "/loanworkflow/loansaction";
    }
    
     @RequestMapping(value = "PreviewSactionOrder", method = RequestMethod.GET)
    public String PreviewSactionOrder(Model model,@RequestParam("taskid") int taskid,@RequestParam("loanid") int loanid){
      LoanForm loan=new LoanForm();
      loan=loanworkflowDao.PreviewSactionOrder(taskid,"00009448",loanid);
     
      model.addAttribute("LoanForm", loan);
       Calendar now = Calendar.getInstance();
      
       int cyear = now.get(Calendar.YEAR);
       int nextYear=cyear+1;
       String fyear=cyear+" - "+nextYear;
      model.addAttribute("curreyear", fyear);
        return "/loanworkflow/previewloansaction";
    }
    
    @RequestMapping(value = "DownloadLoanAttch",method = RequestMethod.GET)
    public void downloadNRCAttachment(HttpServletResponse response,@RequestParam("lid") String loanid){
        
        try{
            String filepath = context.getInitParameter("LoanPath");
            loanworkflowDao.downloadLoanAttachment(response,filepath,loanid);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            
        }
    }
     @RequestMapping(value = "savereapplyLoan", method = {RequestMethod.POST,RequestMethod.GET},params="Save")
    public String savereapplyLoan(@ModelAttribute("LoginUserBean") LoginUserBean lub,@ModelAttribute("LoanForm") LoanForm loanform) {
        String filepath = context.getInitParameter("LoanPath");
        loanworkflowDao.savereapplyLoanData(loanform,lub.getEmpid(),filepath);
       // System.out.println("loan press save page"+lub.getEmpid());
        return "/loanworkflow/loanList";
    }
    
    @RequestMapping(value = "deleteLoanAttachment")
    public String deleteLoanAttachment(@RequestParam("loanId") int lid) {
        String filepath = context.getInitParameter("LoanPath");
        loanworkflowDao.deleteLoanAttch(lid,filepath);
       // System.out.println("loan press save page"+lub.getEmpid());
        return "/loanworkflow/loanList";
    }
    
 // GPF Loan Details    
     @RequestMapping(value = "loangpfapplyController", method = {RequestMethod.POST,RequestMethod.GET})
    public String loanGPFApply(@ModelAttribute("LoginUserBean") LoginUserBean lub,@ModelAttribute("LoanGPFForm") LoanGPFForm loanGPFform, ModelMap model) {
        loanGPFform=loanworkflowDao.GPFEmpDetails(lub.getEmpid());
      
        model.put("LoanGPFForm", loanGPFform);
        return "/loanworkflow/loangpfapply";
    }
    
     @RequestMapping(value = "savegpfLoan", method = {RequestMethod.POST,RequestMethod.GET},params="Save")
    public String savegpfLoan(@ModelAttribute("LoginUserBean") LoginUserBean lub,@ModelAttribute("LoanGPFForm") LoanGPFForm loanGPFform) {
        String filepath = context.getInitParameter("LoangpfPath");
        loanworkflowDao.savegpfLoanData(loanGPFform,lub.getEmpid(),filepath);
       // System.out.println("loan press save page"+lub.getEmpid());
        return "/loanworkflow/loanList";
    }
    
    
     @RequestMapping(value = "gpfApprove", method = RequestMethod.GET)
     public String LoangpfListPage(Model model,@ModelAttribute("LoginUserBean") LoginUserBean lub,@RequestParam("taskId") int taskid){
      LoanGPFForm loan=new LoanGPFForm();
      loan=loanworkflowDao.getgpfLoanDetails(taskid,lub.getEmpid());
     
      model.addAttribute("LoanGPFForm", loan);
        return "/loanworkflow/gpfapprove";
    }
    @RequestMapping(value = "savegpfApproveLoan", method = {RequestMethod.POST,RequestMethod.GET},params="Save")
    public String savegpfApproveLoan(@ModelAttribute("LoginUserBean") LoginUserBean lub,@ModelAttribute("LoanGPFForm") LoanGPFForm LoanGPFForm) {
        
        loanworkflowDao.savegpfApproveLoanData(LoanGPFForm,lub.getEmpid());
       // System.out.println("loan press save page"+lub.getEmpid());
        return "/loanworkflow/loanList";
    }
    
    
     @RequestMapping(value = "ReapplygpfLoan", method = RequestMethod.GET)
    public String ReapplygpfLoan(Model model,@ModelAttribute("LoginUserBean") LoginUserBean lub,@RequestParam("opt") String option,@RequestParam("loanid") int loanid){
      LoanGPFForm loan=new LoanGPFForm();
      loan=loanworkflowDao.ReplygpfLoan(option,lub.getEmpid(),loanid);
     
      model.addAttribute("LoanGPFForm", loan);
        return "/loanworkflow/loangpfreapply";
    }
    
    
    @RequestMapping(value = "savegpfreapplyLoan", method = {RequestMethod.POST,RequestMethod.GET},params="Save")
    public String savegpfreapplyLoan(@ModelAttribute("LoginUserBean") LoginUserBean lub,@ModelAttribute("LoanForm") LoanGPFForm LoanGPFForm) {
         String filepath = context.getInitParameter("LoangpfPath");
        loanworkflowDao.savegpfreapplyLoan(LoanGPFForm,lub.getEmpid(),filepath);
       // System.out.println("loan press save page"+lub.getEmpid());
        return "/loanworkflow/loanList";
    }
    @RequestMapping(value = "SactionGPFOrder", method = RequestMethod.GET)
    public String SactionGPFOrder(Model model,@ModelAttribute("LoginUserBean") LoginUserBean lub,@RequestParam("taskid") int taskid,@RequestParam("loanid") int loanid){
      LoanGPFForm loan=new LoanGPFForm();
      loan=loanworkflowDao.SactionGPFOrder(taskid,loanid);
     
      model.addAttribute("LoanGPFForm", loan);
      
        return "/loanworkflow/loangpfsaction";
    }
     @RequestMapping(value = "saveLoanGPFsaction", method = {RequestMethod.POST,RequestMethod.GET},params="Save")
    public ModelAndView saveLoanGPFsaction(@ModelAttribute("LoginUserBean") LoginUserBean lub,@ModelAttribute("LoanForm") LoanGPFForm LoanGPFForm) {
        
        loanworkflowDao.saveLoanGPFsaction(LoanGPFForm);
       // System.out.println("loan press save page"+lub.getEmpid());
        //return "/loanworkflow/loanList";
        return new ModelAndView("redirect:/SactionGPFOrder.htm?taskid="+LoanGPFForm.getTaskid()+"&loanid="+LoanGPFForm.getLoanId());
    }
    
    
     @RequestMapping(value = "loanhbaapplyController", method = {RequestMethod.POST,RequestMethod.GET})
    public String loanHBAApply(@ModelAttribute("LoginUserBean") LoginUserBean lub,@ModelAttribute("LoanHBAForm") LoanHBAForm LoanHBAForm, ModelMap model) {
        LoanHBAForm=loanworkflowDao.HBAEmpDetails(lub.getEmpid());
      
        model.put("LoanHBAForm", LoanHBAForm);
        return "/loanworkflow/loanHBAapply";
    }
    
    
     @RequestMapping(value = "saveHBALoan", method = {RequestMethod.POST,RequestMethod.GET},params="Save")
    public String saveHBALoan(@ModelAttribute("LoginUserBean") LoginUserBean lub,@ModelAttribute("LoanHBAForm") LoanHBAForm LoanHBAForm) {
        String filepath = context.getInitParameter("LoanHBAPath");
        loanworkflowDao.saveHBALoanData(LoanHBAForm,lub.getEmpid(),filepath);
       // System.out.println("loan press save page"+lub.getEmpid());
        return "/loanworkflow/loanList";
    }
    @RequestMapping(value = "hbaApprove", method = RequestMethod.GET)
     public String LoanhbaListPage(Model model,@ModelAttribute("LoginUserBean") LoginUserBean lub,@RequestParam("taskId") int taskid){
      LoanHBAForm loan=new LoanHBAForm();
      loan=loanworkflowDao.gethbaLoanDetails(taskid);
     
      model.addAttribute("LoanHBAForm", loan);
        return "/loanworkflow/hbaapprove";
    }
     
     
     @RequestMapping(value = "DownloadhbaLoanAttch",method = RequestMethod.GET)
    public void downloadNHBAttachment(HttpServletResponse response,@RequestParam("lid") String loanid,@RequestParam("attchment") String attchmentId){
        
        try{
            String filepath = context.getInitParameter("LoanHBAPath");
            loanworkflowDao.downloadHBALoanAttachment(response,filepath,loanid,attchmentId);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            
        }
    }
    
    @RequestMapping(value = "savehbaApproveLoan", method = {RequestMethod.POST,RequestMethod.GET},params="Save")
    public String savehbaApproveLoan(@ModelAttribute("LoginUserBean") LoginUserBean lub,@ModelAttribute("LoanHBAForm") LoanHBAForm LoanHBAForm) {
        
        loanworkflowDao.savehbaApproveLoanData(LoanHBAForm,lub.getEmpid());
       // System.out.println("loan press save page"+lub.getEmpid());
        return "/loanworkflow/loanList";
    }
    
     @RequestMapping(value = "ReapplyhbaLoan", method = RequestMethod.GET)
    public String ReapplyhbaLoan(Model model,@ModelAttribute("LoginUserBean") LoginUserBean lub,@RequestParam("opt") String option,@RequestParam("loanid") int loanid){
      LoanHBAForm loan=new LoanHBAForm();
      loan=loanworkflowDao.ReplyhbaLoan(option,lub.getEmpid(),loanid);
     
      model.addAttribute("LoanHBAForm", loan);
        return "/loanworkflow/loanhbareapply";
    }
    
    
    @RequestMapping(value = "saveHBAreapplyLoan", method = {RequestMethod.POST,RequestMethod.GET},params="Save")
    public String saveHBAreapplyLoan(@ModelAttribute("LoginUserBean") LoginUserBean lub,@ModelAttribute("LoanHBAForm") LoanHBAForm LoanHBAForm) {
        String filepath = context.getInitParameter("LoanHBAPath");
        loanworkflowDao.saveHBAreapplyLoan(LoanHBAForm,lub.getEmpid(),filepath);
       // System.out.println("loan press save page"+lub.getEmpid());
        return "/loanworkflow/loanList";
    }
    
    @RequestMapping(value = "DownloadPDF")
    public void GeneratePDF(HttpServletResponse response,@RequestParam("loanid") int loanid){
        response.setContentType("application/pdf");
        Document document = new Document(PageSize.A4);
        
        try{
            response.setHeader("Content-Disposition", "attachment; filename=letter.pdf");

            PdfWriter.getInstance(document, response.getOutputStream());
            
            document.open();
            
            loanworkflowDao.viewPDFfunc(document,loanid);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            document.close();
        }
    }
    // GPF Loan Details    
     @RequestMapping(value = "loantempgpfapplyController", method = {RequestMethod.POST,RequestMethod.GET})
    public String loanTempGPFApply(@ModelAttribute("LoginUserBean") LoginUserBean lub,@ModelAttribute("LoanTempGPFForm") LoanTempGPFForm LoanTempGPFForm, ModelMap model) {
       LoanTempGPFForm=loanworkflowDao.TempGPFEmpDetails(lub.getEmpid());
        // System.out.println("Hi");
        model.put("LoanTempGPFForm", LoanTempGPFForm);
        return "/loanworkflow/loantempgpfapply";
    }
     @RequestMapping(value = "savetempgpfLoan", method = {RequestMethod.POST,RequestMethod.GET},params="Save")
    public String savetempgpfLoan(@ModelAttribute("LoginUserBean") LoginUserBean lub,@ModelAttribute("LoanTempGPFForm") LoanTempGPFForm LoanTempGPFForm) {
        String filepath = context.getInitParameter("LoantempgpfPath");
        loanworkflowDao.savetempgpfLoanData(LoanTempGPFForm,lub.getEmpid(),filepath);
       // System.out.println("loan press save page"+lub.getEmpid());
        return "/loanworkflow/loanList";
    }
    @RequestMapping(value = "tempgpfApprove", method = RequestMethod.GET)
     public String LoantempgpfListPage(Model model,@ModelAttribute("LoginUserBean") LoginUserBean lub,@RequestParam("taskId") int taskid){
      LoanTempGPFForm loan=new LoanTempGPFForm();
      loan=loanworkflowDao.tempgpfLoanDetails(taskid);
     
      model.addAttribute("LoanTempGPFForm", loan);
        return "/loanworkflow/tempgpfapprove";
    }
     
     @RequestMapping(value = "DownloadtempgpfLoanAttch",method = RequestMethod.GET)
    public void DownloadtempgpfLoanAttch(HttpServletResponse response,@RequestParam("lid") String loanid){
         System.out.println("loan press save page");
        try{
            String filepath = context.getInitParameter("LoantempgpfPath");
            loanworkflowDao.DownloadtempgpfLoanAttch(response,filepath,loanid);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            
        }
    }
    
    
    @RequestMapping(value = "savetempgpfApprove", method = {RequestMethod.POST,RequestMethod.GET},params="Save")
    public String savetempgpfApprove(@ModelAttribute("LoginUserBean") LoginUserBean lub,@ModelAttribute("LoanTempGPFForm") LoanTempGPFForm LoanTempGPFForm) {
        
        loanworkflowDao.savetempfpfApproveData(LoanTempGPFForm);
       // System.out.println("loan press save page"+lub.getEmpid());
        return "/loanworkflow/loanList";
    }
    
     @RequestMapping(value = "Reapplytempgpf", method = {RequestMethod.POST,RequestMethod.GET})
    public String Reapplytempgpf(Model model,@ModelAttribute("LoginUserBean") LoginUserBean lub,@RequestParam("opt") String option,@RequestParam("loanid") int loanid){
      LoanTempGPFForm loan=new LoanTempGPFForm();
      loan=loanworkflowDao.Reapplytempgpf(option,lub.getEmpid(),loanid);
     
      model.addAttribute("LoanTempGPFForm", loan);
        return "/loanworkflow/tempgpfreapply";
    }
    
    @RequestMapping(value = "savetempgpfreapply", method = {RequestMethod.POST,RequestMethod.GET},params="Save")
    public String savetempgpfreapply(@ModelAttribute("LoginUserBean") LoginUserBean lub,@ModelAttribute("LoanGPFForm") LoanTempGPFForm LoanTempGPFForm) {
        
        String filepath = context.getInitParameter("LoantempgpfPath");
        loanworkflowDao.savetempgpfreapply(LoanTempGPFForm,lub.getEmpid(),filepath);
         return "/loanworkflow/loanList";
    }
}
