/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hrms.model.incrementProposal;

import java.util.Date;

/**
 *
 * @author Surendra
 */
public class ProposalAttr {
    private int proposaldetailId;
    private String empname=null;
    private String gpfno=null;
    private String nextincr=null;
    private String payscale=null;
    private String presentpay=null;
    private String gp=null;
    private String futurepay=null;
    private String presentpaydate=null;
    private String empId=null;
    private String post=null;
    private String ordno="";
    private String orderDate=null;
    

    public int getProposaldetailId() {
        return proposaldetailId;
    }

    public void setProposaldetailId(int proposaldetailId) {
        this.proposaldetailId = proposaldetailId;
    }

    public String getEmpname() {
        return empname;
    }

    public void setEmpname(String empname) {
        this.empname = empname;
    }

    public String getNextincr() {
        return nextincr;
    }

    public void setNextincr(String nextincr) {
        this.nextincr = nextincr;
    }

    public String getPayscale() {
        return payscale;
    }

    public void setPayscale(String payscale) {
        this.payscale = payscale;
    }

    public String getPresentpay() {
        return presentpay;
    }

    public void setPresentpay(String presentpay) {
        this.presentpay = presentpay;
    }

    public String getGp() {
        return gp;
    }

    public void setGp(String gp) {
        this.gp = gp;
    }

    public String getFuturepay() {
        return futurepay;
    }

    public void setFuturepay(String futurepay) {
        this.futurepay = futurepay;
    }

    public String getPresentpaydate() {
        return presentpaydate;
    }

    public void setPresentpaydate(String presentpaydate) {
        this.presentpaydate = presentpaydate;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getGpfno() {
        return gpfno;
    }

    public void setGpfno(String gpfno) {
        this.gpfno = gpfno;
    }

    public String getOrdno() {
        return ordno;
    }

    public void setOrdno(String ordno) {
        this.ordno = ordno;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    

    
    

}
