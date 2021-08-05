/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.model.billvouchingTreasury;

/**
 *
 * @author Surendra
 */
public class ObjectBreakup {
    private int hrmsgeneratedRefno=0;
    private String hrmsgeneratedRefdate=null;
    private String objectHead=null;
    private double objectHeadwiseAmount=0;
    private String treasuryCode=null;

    public void setHrmsgeneratedRefno(int hrmsgeneratedRefno) {
        this.hrmsgeneratedRefno = hrmsgeneratedRefno;
    }

    public int getHrmsgeneratedRefno() {
        return hrmsgeneratedRefno;
    }

    public void setHrmsgeneratedRefdate(String hrmsgeneratedRefdate) {
        this.hrmsgeneratedRefdate = hrmsgeneratedRefdate;
    }

    public String getHrmsgeneratedRefdate() {
        return hrmsgeneratedRefdate;
    }

    public void setObjectHead(String objectHead) {
        this.objectHead = objectHead;
    }

    public String getObjectHead() {
        if(objectHead == null){
            objectHead = "";
        }
        return objectHead;
    }

    public void setObjectHeadwiseAmount(double objectHeadwiseAmount) {
        this.objectHeadwiseAmount = objectHeadwiseAmount;
    }

    public double getObjectHeadwiseAmount() {
        return objectHeadwiseAmount;
    }

    public void setTreasuryCode(String treasuryCode) {
        this.treasuryCode = treasuryCode;
    }

    public String getTreasuryCode() {
        if(treasuryCode == null){
            treasuryCode = "";
        }
        return treasuryCode;
    }

}
