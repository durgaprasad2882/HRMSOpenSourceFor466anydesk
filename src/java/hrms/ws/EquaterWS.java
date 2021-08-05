/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.ws;

import hrms.dao.payroll.schedule.ScheduleDAO;
import hrms.model.payroll.QuaterRent;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 *
 * @author Manas Jena
 */
@WebService
public class EquaterWS {

    ScheduleDAO scheduleDAO;
    
    @WebMethod(exclude=true)
    public void setScheduleDAO(ScheduleDAO scheduleDAO) {
        this.scheduleDAO = scheduleDAO;
    }
    
    @WebMethod(operationName = "getRentData")
    public QuaterRent[] getRentData(@WebParam(name = "month") int month, @WebParam(name = "year") int year) {
        System.out.println("month");
        return scheduleDAO.getRentData(month, year);
    }
}
