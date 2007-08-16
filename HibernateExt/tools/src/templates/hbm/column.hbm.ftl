<#if column.isFormula()>
<formula>${column.getFormula()}</formula>
<#else>
<column name="${column.quotedName}" ${c2h.columnAttributes(column)}<#if column.comment?exists>>
<comment>${column.comment}</comment>
</column><#else>/>
</#if>
</#if>