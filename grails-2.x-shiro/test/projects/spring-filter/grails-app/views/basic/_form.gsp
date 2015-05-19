<div class="fieldcontain ${hasErrors(bean: basicInstance, field: 'name', 'error')} ">
  <label for="name">
    <g:message code="basic.name.label" default="Name"/>

  </label>
  <g:textField name="name" value="${basicInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: basicInstance, field: 'intValue', 'error')} required">
  <label for="intValue">
    <g:message code="basic.intValue.label" default="Int Value"/>
    <span class="required-indicator">*</span>
  </label>
  <g:field name="intValue" type="number" value="${basicInstance.intValue}" required=""/>
</div>

