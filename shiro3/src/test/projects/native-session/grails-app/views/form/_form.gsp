



<div class="fieldcontain ${hasErrors(bean: formInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="form.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${formInstance?.name}"/>
</div>

