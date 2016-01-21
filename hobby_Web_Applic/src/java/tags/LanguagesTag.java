/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tags;

import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.commons.db.SQLDatabase;
import be.hepl.benbear.shopdb.Language;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 *
 * @author Bears
 */
public class LanguagesTag extends SimpleTagSupport {

    private Database database; 
    private List<Language> languages;
    
    @Override
    public void doTag() throws JspException {
        JspWriter out = getJspContext().getOut();
    
        Database.Driver.ORACLE.load();
        database = new SQLDatabase();
        database.registerClass(
            Language.class
        );
        database.connect("jdbc:oracle:thin:@178.32.41.4:8080:xe", "dbshop", "bleh");

        try {
            languages = database.table(Language.class).find().get().collect(Collectors.<Language>toList());
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(LanguagesTag.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            

            JspFragment f = getJspBody();
            if (f != null) {
                f.invoke(out);
            }
            
            for(Language l : languages) {
                out.print("<form method=\"post\" action=\"ShopServlet\">");
                out.print("<input type=\"hidden\" name=\"type\" value=\"language\">");
                out.print("<input type=\"hidden\" name=\"locale\" value=\"" + l.getLanguageId() + "\">");                
                out.print("<input type=\"submit\" value=\""+ l.getLanguageName() + "\">");
                out.print("</form>");
            }
            
        } catch (java.io.IOException ex) {
            throw new JspException("Error in LanguagesTag tag", ex);
        }
    }
    
}
