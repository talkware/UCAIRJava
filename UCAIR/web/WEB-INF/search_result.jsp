<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="search_result ${item.cssClass}">
	<div class="search_result_title">
		<c:url value="click" var="clickUrl">
			<c:param name="search" value="${item.searchId}" />
			<c:param name="pos" value="${item.pos}" />
		</c:url>
		<span style="display: none"><c:out value="${clickUrl}" /></span><a
			href="${item.url}">${item.title}</a>
	</div>
	<div class="search_result_summary">${item.summary}</div>
	<div class="search_result_meta">
		<span class="search_result_url">${item.displayUrl}</span> <span
			class="search_result_pos">- ${item.pos}</span>
	</div>
</div>