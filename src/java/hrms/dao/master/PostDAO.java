/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.master;

import hrms.model.master.GPost;
import java.util.ArrayList;

/**
 *
 * @author Manas Jena
 */
public interface PostDAO {

    public ArrayList getPostList(String departmentCode);

    public ArrayList getCadrewisePostList(String departmentCode, String cadreCode);

    public ArrayList getPostList(String departmentCode, String offcode);

    public String getPostName(String postcode);
    
    public String savePost(GPost post);
    
    public GPost getPostDetail(String postcode);

}
