<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
    response.setHeader("Cache-Control", "no-store");
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link rel="stylesheet" type="text/css" href="static/search.css" />

<title>UCAIR - ${search.query}</title>

<script type="text/javascript"
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.9.0/jquery.min.js"></script>

<script type="text/javascript">
	//<![CDATA[

	var searchId = "${search.searchId}";
	var pageId = "${search.searchId}";
	var timestamp =
<%=System.currentTimeMillis()%>
	;

	function saveScrollPos(pageId, refElement) {
		var scrollPos = $(window).scrollTop();
		if (scrollPos > 0) {
			if (refElement) {
				scrollPos = refElement.offset().top - scrollPos;
			}
			sessionStorage.setItem(pageId + ".scroll", scrollPos);
		}
	}

	function loadScrollPos(pageId, refElement) {
		var scrollPos = sessionStorage.getItem(pageId + ".scroll");
		if (scrollPos) {
			scrollPos = parseInt(scrollPos);
			if (refElement) {
				scrollPos = refElement.offset().top - scrollPos;
			}
			$(window).scrollTop(scrollPos);
			sessionStorage.removeItem(pageId + ".scroll");
		}
	}

	function reloadPageIfVisited() {
		var storedTimestamp = sessionStorage.getItem(pageId + ".timestamp");
		if (storedTimestamp && timestamp <= parseInt(storedTimestamp)) {
			location.reload();
		} else {
			sessionStorage.setItem(pageId + ".timestamp", timestamp.toString());
		}
	}

	$(document).ready(function() {
		if (searchId) {
			reloadPageIfVisited();
			loadScrollPos(pageId, $("#search_results"));
		}

		$(".search_result_title > a").mousedown(function() {
			var href = $(this).prev().text();
			$(this).attr('href', href);
		}).click(function() {
			if (searchId) {
				saveScrollPos(pageId, $("#search_results"));
			}
			return true;
		});
	});

	//]]>
</script>

</head>
<body>

	<div id="top">
		<form id="search_box" method="get">
			<img id="logo" src="static/logo.png" alt="UCAIR logo" /> <input
				id="query_input" type="text" name="query" value="${search.query}"
				size="50" /> <input type="submit" value="Search" />
		</form>
	</div>

	<div id="main">
		<div id="result_count">
			<c:if test="${search.totalResultCountEstimate eq 0}">No result</c:if>
			<c:if test="${search.totalResultCountEstimate gt 0}">Results ${startPos} - ${endPos}</c:if>
		</div>

		<div id="search_results">
			<c:forEach var="item" items="${searchResults}">
				<c:set var="item" value="${item}" scope="request" />
				<c:import url="${item.jspResource}" />
			</c:forEach>
		</div>

		<div id="page_nav">
			<c:if test="${prevPage gt 0}">
				<c:url value="search" var="prevPageUrl">
					<c:param name="search" value="${search.searchId}" />
					<c:param name="page" value="${prevPage}" />
				</c:url>
				<a href="${prevPageUrl}">Prev</a>
			</c:if>
			<c:if test="${nextPage gt 0}">
				<c:url value="search" var="nextPageUrl">
					<c:param name="search" value="${search.searchId}" />
					<c:param name="page" value="${nextPage}" />
				</c:url>
				<a href="${nextPageUrl}">Next</a>
			</c:if>
		</div>
	</div>

	<div id="side">
		<c:if test="${searchModelWidget != null}">
			<c:set var="widget" value="${searchModelWidget}" scope="request" />
			<c:import url="${widget.jspResource}" />
		</c:if>
	</div>

	<jsp:include page="footer.jsp" />

</body>
</html>