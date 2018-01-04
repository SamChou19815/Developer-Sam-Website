<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/custom.tld" prefix="t" %>
<%@ taglib uri="/WEB-INF/blogCustom.tld" prefix="blogT" %>
<!DOCTYPE HTML>
<html>
<head>
    <blogT:BlogHead title="${requestScope.title}"/>
</head>
<body>
<div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <t:HeaderWithBackButton title="Blog Article" navButtonURL="blog"/>
    <main class="mdl-layout__content">
        <blogT:BlogArticleContentCard blogArticle="${requestScope.article}"/>
        <c:forEach items="${requestScope.comments}" var="blogComment">
            <blogT:BlogCommentCard blogComment="${blogComment}"/>
        </c:forEach>
        <blogT:BlogAddCommentCard url="${param.url}"/>
    </main>
</div>
</body>
</html>
<script>
    var url = "${param.url}";
    var Controller = {
        submitComment: function () {
            $('#progress-submit-comment').show();
            var submittedData = {
                url: url,
                anonymous: $('#anonymous').is(':checked'),
                comment: $('#newComment').val()
            };
            $.post("apps/blog/submitComment", submittedData, function (data) {
                if (data === "success") {
                    location.reload();
                }else {
                    alert(data);
                }
            });
        },
        deleteComment: function (key) {
            $.post("apps/blog/deleteComment", {url: url, key: key}, function (data) {
                if (data === "success") {
                    $('#comment-' + key).remove();
                }else {
                    alert(data);
                }
            });
        }
    }
</script>