package tags;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class DateTimeTag extends SimpleTagSupport {
    private String language;
    private String chDate;

    @Override
    public void doTag() throws JspException {
        JspWriter out = getJspContext().getOut();
        
        try {
            JspFragment f = getJspBody();
            if (f != null) {
                f.invoke(out);
            }
            
            Date maintenant = new Date();
            if ("en_UK".equals(language))
                chDate = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL,Locale.UK). format(maintenant);
            else if ("fr_FR".equals(language))
                chDate = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL,Locale.FRANCE).format(maintenant);
            else 
                chDate = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL,Locale.US).format(maintenant);

            out.println(chDate);
        } catch (java.io.IOException ex) {
            throw new JspException("Error in DateTimeTag tag", ex);
        }
    }

    public void setLanguage(String language) {
        this.language = language;
    }
    
}
