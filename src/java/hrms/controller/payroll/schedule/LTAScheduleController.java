/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.payroll.schedule;

import hrms.SelectOption;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Surendra
 */
@Controller
public class LTAScheduleController {

    @RequestMapping(value = "ltaSchduleHTML", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView ltaSchduleHTML() {

        ModelAndView mav = null;
        try {
            mav = new ModelAndView();

            mav.setViewName("/payroll/schedule/LtaScheduleInterface");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @ResponseBody
    @RequestMapping(value = "getYearList", method = {RequestMethod.GET, RequestMethod.POST})
    public void getYearList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        ArrayList yearlist = new ArrayList();
        SelectOption so=null;
        int curyear=0;
        Calendar cal=Calendar.getInstance();
        curyear=cal.get(Calendar.YEAR);
        for(int i=curyear;i>curyear-3;i--){
            so=new SelectOption();
            so.setLabel(i+"");
            so.setValue(i+"");
            yearlist.add(so);
        }
        JSONArray json = new JSONArray(yearlist);
        out = response.getWriter();
        out.write(json.toString());
    }
}
