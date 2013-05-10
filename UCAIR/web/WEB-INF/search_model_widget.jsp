<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="widget" id="searchModelWidget">
    <div class="widget_head">${widget.modelName}</div>
	<table>
		<c:forEach var="term" items="${widget.model}">
			<tr>
				<td>${term.key}</td>
				<td><fmt:formatNumber type="number" maxIntegerDigits="4"
						value="${term.value}" /></td>
			</tr>
		</c:forEach>
	</table>
</div>