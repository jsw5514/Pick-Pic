package tags;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

public class BackButtonTag extends TagSupport {
    @Override
    public int doStartTag() throws JspException {
        try {
            pageContext.getOut().print("<script>history.back();</script>");
        } catch (Exception e) {
            throw new JspException("Error: " + e.getMessage());
        }
        return SKIP_BODY;
    }
}

