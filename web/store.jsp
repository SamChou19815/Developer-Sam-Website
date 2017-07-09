<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/custom.tld" prefix="t" %>
<!DOCTYPE HTML>
<html>
<head>
    <t:Head title="Store - Developer Sam"/>
</head>
<body>
<div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
    <t:Header title="Store - Developer Sam" selected="3"/>
    <main class="mdl-layout__content">
        <t:Card title="Notes Selling Guide">
            <t:CardText>
                I intend to sell some of my review notes on the site. <br>
                I will give a target, for example $10, for a review material. <br>
                I will wait until someone or some group gives me the money and
                I will give the material to that person or group. <br>
                That person or group is allowed to sell this for profit.
                The action is neither encouraged nor discouraged.
            </t:CardText>
        </t:Card>
        <t:Card title="IB Notes Bundle (Chinese+History+Physics)">
            <t:CardText>FREE!</t:CardText>
            <t:CardActions>
                <t:LinkButton href="resource/store/open/IB_Notes/NotesBundle.zip" openInNewTab="true">
                    Download
                </t:LinkButton>
            </t:CardActions>
        </t:Card>
        <t:Card title="AP Computer Science Review Material">
            <t:CardText>Target: 50 RMB</t:CardText>
            <t:CardActions>
                <t:LinkButton href="resource/store/open/APCS/2.ClassesandObjects.pdf" openInNewTab="true">
                    See Samples
                </t:LinkButton>
                <t:LinkButton href="mailto:sam@com.developersam.com" openInNewTab="true">
                    Contact Me
                </t:LinkButton>
            </t:CardActions>
        </t:Card>
    </main>
</div>
</body>
</html>

