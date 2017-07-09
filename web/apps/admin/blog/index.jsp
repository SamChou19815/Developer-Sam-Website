<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/custom.tld" prefix="t" %>
<!DOCTYPE HTML>
<html>
<head>
    <t:Head title="Blog Admin - Developer Sam Apps"/>
</head>
<body>
<div class="mdl-layout mdl-js-layout mdl-layout--fixed-header mdl-layout--fixed-tabs">
    <t:HeaderWithoutDrawer title="Blog Admin - Developer Sam Apps"/>
    <main class="mdl-layout__content app">
        <t:Card title="Create/Update Blog Post">
            <t:CardText>
                <t:LineInput id="url">Input URL segment that helps to identify the source.</t:LineInput>
                <t:LineInput id="title">Title of the blog post.</t:LineInput>
            </t:CardText>
            <t:CardActions>
                <t:LinkButton href="#" openInNewTab="false"
                              onClick="Controller.loadContent();">Load Content</t:LinkButton>
                <t:LinkButton href="#" openInNewTab="false"
                              onClick="Controller.createOrUpdate();">Create/Update</t:LinkButton>
            </t:CardActions>
        </t:Card>
        <t:Card title="Loaded Content" customClasses="blogArticleCard">
            <t:CardText id="loaded-content" customClasses="blogArticleContent"/>
        </t:Card>
        <t:Card title="Statistics">
            <t:CardText id="blog-statistics"/>
        </t:Card>
        <t:Card title="Help">
            <t:CardText>
                This blog admin system does not use WYSIWYG editors.
                <br>Instead, users must write HTML to their blog posts.
                <br>HTMLs are written and stored at /resource/blog/posts/.
            </t:CardText>
        </t:Card>
    </main>
</div>
</body>
</html>
<script>
    var Controller = {
        loadContent: function () {
            $.post("loadContent", {url: $('#url').val()}, function (data) {
                $('#loaded-content').html(data);
            });
        },
        createOrUpdate: function () {
            $.post("createOrUpdate", {url: $('#url').val(), title: $('#title').val()}, function (data) {
                alert(data);
            });
        },
        loadStatistics: function () {
            $.get("loadStatistics", function (data) {
                data = JSON.parse(data);
                var obj = $('#blog-statistics');
                for (var i = 0; i < data.length; i += 2) {
                    var title = data[i], count = data[i + 1];
                    obj.append("<div>");
                    obj.append("Title: " + title + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Count: " + count);
                    obj.append("</div>");
                }
            });
        }
    };
    Controller.loadStatistics();
</script>