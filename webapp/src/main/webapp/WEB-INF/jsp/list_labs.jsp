<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%--@elvariable id="course" type="org.lakunu.web.models.Course"--%>
<%--@elvariable id="courseLabs" type="java.util.List<org.lakunu.web.models.Lab>"--%>
<%--@elvariable id="labPermissions" type="java.util.Map<String,String>"--%>
<c:if test="${not empty labPermissions}">
    <div id="publishResponse"></div>
    <table class="table table-striped">
        <thead class="thead-inverse">
        <tr>
            <th>Name</th>
            <th>Description</th>
            <th>Created</th>
            <th>Ready</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${courseLabs}" var="lab">
            <c:if test="${not empty labPermissions[lab.id]}">
                <tr>
                    <td><c:out value="${lab.name}"/></td>
                    <td><c:out value="${lab.description}"/></td>
                    <td><c:out value="${lab.createdAt}"/></td>
                    <td>
                        <c:if test="${lab.published}">
                            Yes
                        </c:if>
                        <c:if test="${not lab.published}">
                            No
                        </c:if>
                    </td>
                    <td>
                        <c:if test="${fn:contains(labPermissions[lab.id], 'u') || fn:contains(labPermissions[lab.id], 'p')}">
                            <a href="/lab/${course.id}/${lab.id}">View</a>
                            <span class="tab-space">&nbsp;</span>
                        </c:if>
                        <c:if test="${fn:contains(labPermissions[lab.id], 's')}">
                            <c:if test="${lab.published}">
                                <a href="/submit/${course.id}/${lab.id}">Submit</a>
                            </c:if>
                            <c:if test="${not lab.published}">
                                <span class="text-muted">Submit</span>
                            </c:if>
                            <span class="tab-space">&nbsp;</span>
                        </c:if>
                        <a href="/submission/${course.id}/${lab.id}?limit=3">Results</a>
                        <span class="tab-space">&nbsp;</span>
                        <c:if test="${fn:contains(labPermissions[lab.id], 'g')}">
                            <a href="/grading/${course.id}/${lab.id}">Grade</a>
                        </c:if>
                    </td>
                </tr>
            </c:if>
        </c:forEach>
        </tbody>
    </table>
</c:if>
<c:if test="${empty labPermissions}">
    <p>No labs to display</p>
</c:if>
