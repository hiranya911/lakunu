<%--@elvariable id="lab" type="org.lakunu.web.data.Lab"--%>
<%--@elvariable id="course" type="org.lakunu.web.data.Course"--%>
<%--@elvariable id="submissions" type="java.util.List<org.lakunu.web.models.SubmissionView>"--%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <%@ include file="/WEB-INF/include/header.html" %>
    <title>Lakunu: Evaluations [${lab.name}]</title>
    <style>
        span.label-success {
            background-color: #009933;
        }
    </style>
</head>
<body>
<%@ include file="/WEB-INF/include/body_top.html" %>
<div class="container">
    <h3>${lab.name} Submissions <small><a href="/course/${lab.courseId}">(Back to ${course.name})</a></small></h3>
    <c:if test="${empty submissions}">
        <div class="row">
            <div class="col-md-12">
                <div class="alert alert-warning">
                    You don't have any submissions for this lab yet.
                </div>
            </div>
        </div>
        <c:if test="${lab.openForSubmissions}">
            <shiro:hasPermission name="lab:submit:${lab.courseId}:${lab.id}">
                <div class="row">
                    <div class="col-md-12">
                        <a href="/submit/${lab.courseId}/${lab.id}">Make Submission</a>
                    </div>
                </div>
            </shiro:hasPermission>
        </c:if>
    </c:if>
    <c:forEach items="${submissions}" var="sub">
        <div class="row">
            <div class="col-md-12">
                <div class="panel panel-primary">
                    <div class="panel-heading">Submission at: ${sub.submittedAt} <span class="pull-right">(Last score: ${sub.finalScore})</span></div>
                    <div class="panel-body">
                        <c:if test="${empty sub.evaluations}">
                            <span class="label label-info">Pending Evaluation</span>
                        </c:if>
                        <c:forEach items="${sub.evaluations}" var="eval">
                            <table class="table table-striped">
                                <c:if test="${eval.finishingStatus eq 'SUCCESS'}">
                                    <tr class="success">
                                        <th colspan="2">Evaluation Result: Success</th>
                                    </tr>
                                </c:if>
                                <c:if test="${eval.finishingStatus eq 'FAILED'}">
                                    <tr class="danger">
                                        <td colspan="2">Evaluation Result: Failed</td>
                                    </tr>
                                </c:if>
                                <tr>
                                    <c:set var="percentage" value="${eval.totalScore.percentage}"/>
                                    <td class="col-md-3">Total score:</td>
                                    <td>
                                        <c:if test="${percentage ge 70}">
                                            <span class="label label-success">${eval.totalScore}</span>
                                        </c:if>
                                        <c:if test="${percentage ge 40 && percentage lt 70}">
                                            <span class="label label-warning">${eval.totalScore}</span>
                                        </c:if>
                                        <c:if test="${percentage lt 40}">
                                            <span class="label label-danger">${eval.totalScore}</span>
                                        </c:if>
                                    </td>
                                </tr>
                                <tr>
                                    <td>Grading Rubric:</td>
                                    <td>
                                        <ul class="list-group">
                                            <c:forEach items="${eval.scores}" var="score">
                                                <li>${score.name} <span class="badge">${score}</span></li>
                                            </c:forEach>
                                        </ul>
                                    </td>
                                </tr>
                                <tr>
                                    <td>Started at:</td>
                                    <td>${eval.startedAt}</td>
                                </tr>
                                <tr>
                                    <td>Finished at:</td>
                                    <td>${eval.finishedAt}</td>
                                </tr>
                                <tr>
                                    <td>Log:</td>
                                    <td>
                                        <div id="showLogModal-${eval.id}" class="modal fade" role="dialog">
                                            <div class="modal-dialog modal-lg">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                                                        <h4 class="modal-title">Log [Evaluation ${eval.id}]</h4>
                                                    </div>
                                                    <div class="modal-body">
                                                        <textarea cols="100" rows="20" style="font-family: monospace;" readonly>${eval.htmlSafeLog}</textarea>
                                                    </div>
                                                    <div class="modal-footer">
                                                        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <a href="#" data-toggle="modal" data-target="#showLogModal-${eval.id}">View</a>
                                    </td>
                                </tr>
                            </table>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </c:forEach>
    <c:if test="${not viewAll && not empty submissions}">
        <a href="/submission/${course.id}/${lab.id}">View All</a>
    </c:if>
</div>
</body>
</html>