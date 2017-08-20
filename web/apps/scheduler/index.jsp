<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/custom.tld" prefix="t" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/schedulerCustom.tld" prefix="schedulerT" %>
<!DOCTYPE HTML>
<html>
<head>
    <t:Head title="Scheduler - Developer Sam Apps"/>
</head>
<body>
<div class="mdl-layout mdl-js-layout mdl-layout--fixed-header mdl-layout--fixed-tabs">
    <t:Header title="Scheduler - Developer Sam Apps" selected="4"/>
    <main class="mdl-layout__content app">
        <schedulerT:SchedulerAddItemCard/>
        <c:forEach items="${requestScope.schedulerItems}" var="schedulerItem">
            <schedulerT:SchedulerItemCard schedulerItem="${schedulerItem}" />
        </c:forEach>
    </main>
</div>
</body>
</html>
<script>
    var Controller = {
        addItem: function () {
            var description = $("#item-description").val(), deadline = $("#item-deadline").val();
            $.post("add", {description: description, deadline: deadline}, function (data) {
                if (data === "true") {
                    alert("You have successfully added a new scheduler item. The page is going to reload.");
                    location.reload();
                }else {
                    alert("You have not given the description OR the date you've given is illegal.");
                }
            })
        }
    }
</script>