/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.payroll.schedule;

import hrms.dao.payroll.schedule.PayBillDMPDAO;
import hrms.dao.payroll.schedule.ScheduleDAO;
import hrms.model.common.CommonReportParamBean;
import hrms.model.payroll.schedule.VoucherSlipBean;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class VoucherSlipScheduleController implements ServletContextAware {

    @Autowired
    public ScheduleDAO comonScheduleDao;

    @Autowired
    public PayBillDMPDAO paybillDmpDao;

    private ServletContext context;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }

    @RequestMapping(value = "VoucherSlipHTML", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView VoucherSlipHTML(@RequestParam("billNo") String billNo) {

        ModelAndView mav = null;
        VoucherSlipBean voucherBean = new VoucherSlipBean();
        try {
            mav = new ModelAndView();
            CommonReportParamBean crb = paybillDmpDao.getCommonReportParameter(billNo);

            if (crb.getBillType() != null && crb.getBillType().equals("43")) {
                voucherBean = comonScheduleDao.getVoucherSlipScheduleDetails(billNo, crb.getAqyear(), crb.getAqmonth());

                    System.out.println("==="+voucherBean.getDemandNo());
                mav.addObject("VoucherHeadr", voucherBean);
                mav.setViewName("/payroll/arrear/VoucherSlipArrear");
            } else {
                voucherBean = comonScheduleDao.getVoucherSlipScheduleDetails(billNo, crb.getAqyear(), crb.getAqmonth());

                mav.addObject("VoucherHeadr", voucherBean);
                mav.setViewName("/payroll/schedule/VoucherSlip");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

}
