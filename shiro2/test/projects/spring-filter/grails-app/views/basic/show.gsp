<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'basic.label', default: 'Basic')}"/>
  <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<a href="#show-basic" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                            default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
  <ul>
    <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
    <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>
  </ul>
</div>

<div id="show-basic" class="content scaffold-show" role="main">
  <h1><g:message code="default.show.label" args="[entityName]"/></h1>
  <g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
  </g:if>
  <ol class="property-list basic">

    <g:if test="${basicInstance?.name}">
      <li class="fieldcontain">
        <span id="name-label" class="property-label"><g:message code="basic.name.label" default="Name"/></span>

        <span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${basicInstance}"
                                                                                field="name"/></span>

      </li>
    </g:if>

    <g:if test="${basicInstance?.intValue}">
      <li class="fieldcontain">
        <span id="intValue-label" class="property-label"><g:message code="basic.intValue.label"
                                                                    default="Int Value"/></span>

        <span class="property-value" aria-labelledby="intValue-label"><g:fieldValue bean="${basicInstance}"
                                                                                    field="intValue"/></span>

      </li>
    </g:if>

  </ol>
  <g:form>
    <fieldset class="buttons">
      <g:hiddenField name="id" value="${basicInstance?.id}"/>
      <g:link class="edit" action="edit" id="${basicInstance?.id}"><g:message code="default.button.edit.label"
                                                                              default="Edit"/></g:link>
      <g:actionSubmit class="delete" action="delete"
                      value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                      onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
    </fieldset>
  </g:form>
</div>
</body>
</html>
