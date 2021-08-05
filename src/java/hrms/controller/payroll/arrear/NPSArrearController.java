package hrms.controller.payroll.arrear;

import hrms.common.Numtowordconvertion;
import hrms.dao.payroll.arrear.NPSArrearDAO;
import hrms.dao.payroll.schedule.PayBillDMPDAO;
import hrms.dao.payroll.schedule.ScheduleDAO;
import hrms.model.common.CommonReportParamBean;
import hrms.model.payroll.schedule.BillContributionRepotBean;
import java.util.List;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class NPSArrearController {

    @Autowired
    public NPSArrearDAO npsArrearDAO;

    @Autowired
    public PayBillDMPDAO paybillDmpDao;

    @RequestMapping(value = "NPSArrear")
    public ModelAndView NPSArrear(@RequestParam("billNo") String billNo) {

        ModelAndView mav = null;
        List empDataList = null;
        String cpfTotal = "";
        String gcpfTotal = "";
        String cpfTotalFig = "";
        String gcpfTotalFig = "";
        String gTotal = "";
        BillContributionRepotBean billContBean = new BillContributionRepotBean();
        try {
            mav = new ModelAndView();

            CommonReportParamBean crb = paybillDmpDao.getCommonReportParameter(billNo);
            billContBean = npsArrearDAO.getBillContributionRepotScheduleHeaderDetails("annexure1", billNo);
            empDataList = npsArrearDAO.getBillContributionRepotScheduleEmpList("annexure1", billNo, crb.getAqyear(), crb.getAqmonth());

            BillContributionRepotBean obj = null;
            if (empDataList != null && empDataList.size() > 0) {
                obj = new BillContributionRepotBean();
                for (int i = 0; i < empDataList.size(); i++) {
                    obj = (BillContributionRepotBean) empDataList.get(i);

                    //cpfTotal = obj.getTotCaryFrd();
                    gcpfTotal = obj.getTotCaryFrd();
                    gTotal = obj.getGrandTotal();
                    System.out.println("CPF Total is: "+cpfTotal);
                    //cpfTotalFig = Numtowordconvertion.convertNumber((int) Integer.parseInt(cpfTotal));
                    gcpfTotalFig = Numtowordconvertion.convertNumber((int) Integer.parseInt(gcpfTotal));
                }
            }
            mav.addObject("NpsEmpList", empDataList);
            mav.addObject("NPSHeader", billContBean);

            mav.addObject("TotCpf", cpfTotal);
            mav.addObject("TotCpfFig", cpfTotalFig);
            mav.addObject("TotGcpf", gcpfTotal);
            mav.addObject("TotGcpfFig", gcpfTotalFig);
            mav.addObject("GrandTot", gTotal);

            mav.setViewName("/payroll/arrear/NPSArrear");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "NPSArrear2")
    public ModelAndView NPSArrear2(@RequestParam("billNo") String billNo) {

        ModelAndView mav = null;
        List empDataList = null;
        String cpfTotal = "";
        String gcpfTotal = "";
        String cpfTotalFig = "";
        String gcpfTotalFig = "";
        String gTotal = "";
        BillContributionRepotBean billContBean = new BillContributionRepotBean();
        try {
            mav = new ModelAndView();

            CommonReportParamBean crb = paybillDmpDao.getCommonReportParameter(billNo);
            billContBean = npsArrearDAO.getBillContributionRepotScheduleHeaderDetails("annexure1", billNo);
            empDataList = npsArrearDAO.getBillContributionRepotScheduleEmpList("annexure2", billNo, crb.getAqyear(), crb.getAqmonth());

            BillContributionRepotBean obj = null;
            if (empDataList != null && empDataList.size() > 0) {
                obj = new BillContributionRepotBean();
                for (int i = 0; i < empDataList.size(); i++) {
                    obj = (BillContributionRepotBean) empDataList.get(i);

                    //cpfTotal = obj.getTotCpf();
                    gcpfTotal = obj.getTotCaryFrd();
                    gTotal = obj.getGrandTotal();
                    //cpfTotalFig = Numtowordconvertion.convertNumber((int) Integer.parseInt(cpfTotal));
                    gcpfTotalFig = Numtowordconvertion.convertNumber((int) Integer.parseInt(gcpfTotal));
                }
            }
            mav.addObject("NpsEmpList", empDataList);
            mav.addObject("NPSHeader", billContBean);

            mav.addObject("TotCpf", cpfTotal);
            mav.addObject("TotCpfFig", cpfTotalFig);
            mav.addObject("TotGcpf", gcpfTotal);
            mav.addObject("TotGcpfFig", gcpfTotalFig);
            mav.addObject("GrandTot", gTotal);

            mav.setViewName("/payroll/arrear/Annexure2");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "NPSArrear3")
    public ModelAndView NPSArrear3(@RequestParam("billNo") String billNo) {

        ModelAndView mav = null;
        List empDataList = null;
        String cpfTotal = "";
        String gcpfTotal = "";
        String cpfTotalFig = "";
        String gcpfTotalFig = "";
        String gTotal = "";
        BillContributionRepotBean billContBean = new BillContributionRepotBean();
        try {
            mav = new ModelAndView();

            CommonReportParamBean crb = paybillDmpDao.getCommonReportParameter(billNo);
            billContBean = npsArrearDAO.getBillContributionRepotScheduleHeaderDetails("annexure1", billNo);
            empDataList = npsArrearDAO.getBillContributionRepotScheduleEmpList("annexure3", billNo, crb.getAqyear(), crb.getAqmonth());

            BillContributionRepotBean obj = null;
            if (empDataList != null && empDataList.size() > 0) {
                obj = new BillContributionRepotBean();
                for (int i = 0; i < empDataList.size(); i++) {
                    obj = (BillContributionRepotBean) empDataList.get(i);

                    //cpfTotal = obj.getTotCpf();
                    gcpfTotal = obj.getTotCaryFrd();
                    gTotal = obj.getGrandTotal();
                    //cpfTotalFig = Numtowordconvertion.convertNumber((int) Integer.parseInt(cpfTotal));
                    gcpfTotalFig = Numtowordconvertion.convertNumber((int) Integer.parseInt(gcpfTotal));
                }
            }
            mav.addObject("NpsEmpList", empDataList);
            mav.addObject("NPSHeader", billContBean);

            mav.addObject("TotCpf", cpfTotal);
            mav.addObject("TotCpfFig", cpfTotalFig);
            mav.addObject("TotGcpf", gcpfTotal);
            mav.addObject("TotGcpfFig", gcpfTotalFig);
            mav.addObject("GrandTot", gTotal);

            mav.setViewName("/payroll/arrear/Annexure3");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

}
