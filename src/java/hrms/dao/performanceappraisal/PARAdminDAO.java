/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hrms.dao.performanceappraisal;

import hrms.model.parmast.PARSearchResult;

/**
 *
 * @author DurgaPrasad
 */
public interface PARAdminDAO {
    public PARSearchResult getPARList(String fiscalyear, int limit, int offset);
    public PARSearchResult getPARList(String fiscalyear, String empid, int limit, int offset);
}
