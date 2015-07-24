<%@page import="org.apache.shiro.grails.ShiroBasicPermission" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="secure" />
  <title>Test Index Page</title>
</head>
<body>
  <div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
  </div>
  <div class="body">
    <h1>Test</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <div class="list">
      <ul>
        <shiro:hasPermission permission="${new ShiroBasicPermission('test', ['show'])}"><li><g:link action="show">Show</g:link></li></shiro:hasPermission>
        <shiro:hasPermission permission="${new ShiroBasicPermission('test', ['create'])}"><li><g:link action="create">Create</g:link></li></shiro:hasPermission>
        <shiro:hasPermission permission="${new ShiroBasicPermission('test', ['edit'])}"><li><g:link action="edit">Edit</g:link></li></shiro:hasPermission>
        <shiro:hasPermission permission="${new ShiroBasicPermission('test', ['delete'])}"><li><g:link action="delete">Delete</g:link></li></shiro:hasPermission>
    </div>
    <shiro:hasPermission permission="comments:view"><h2>Comments</h2></shiro:hasPermission>
    <shiro:hasPermission permission="comments:edit"><h2>Edit a user comment</h2></shiro:hasPermission>
    <shiro:hasPermission permission="tags"><h2>Tags</h2></shiro:hasPermission>
  </div>
</body>
</html>
