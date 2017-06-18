<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>

<html>
<head>
    <title>Список задач</title>
</head>
<body>

<table>
    <c:forEach items="${jobs}" var="job">

        <tr>
            <td><a href="/run-job/${job.name }">${job.name }</a></td>
            <td><span> ${job.lastTimeStarted}</span></td>
            <td><c:choose>
                <c:when test="${job.running}">
                    <span style="color:red">RUNNING</span>;
                </c:when>
                <c:otherwise>
                    <span>${job.lastTimeFinished}</span>
                </c:otherwise>
            </c:choose>
            </td>
        </tr>
    </c:forEach>

</table>
</body>
</html>