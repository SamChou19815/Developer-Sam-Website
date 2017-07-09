<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/custom.tld" prefix="t" %>
<%@ taglib uri="/WEB-INF/blogCustom.tld" prefix="blogT" %>
<!DOCTYPE HTML>
<html>
<head>
    <blogT:BlogHead title="Blog - Developer Sam"/>
</head>
<body>
<div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <t:Header title="Blog - Developer Sam" selected="1"/>
    <main class="mdl-layout__content">
        <c:forEach items="${requestScope.blogArticles}" var="blogArticle">
            <blogT:BlogArticleSummaryCard blogArticle="${blogArticle}" />
        </c:forEach>
    </main>
</div>
</body>
</html>
