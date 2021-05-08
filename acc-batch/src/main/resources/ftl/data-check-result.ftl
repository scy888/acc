<!DOCTYPE html>
<html lang='zh-CN'>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<head>
    <title>${subject!}</title>
    <style>
        th {
            white-space: nowrap;
        }
    </style>
</head>
<body>
<div style="line-height: 180%;">
    您好：<br>
    ${content!}
    <table border="1" cellspacing="0">
        <thead>
        <tr>
            <th>要校验的分类编号</th>
            <th>校验规则说明</th>
            <th>校验是否通过</th>
            <th>校验不通过的数据量</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <#list dataCheckResultList as dataCheckResult>
        <tr>
            <td>${dataCheckResult.name!}</td>
            <td>${dataCheckResult.description!}</td>
            <td <#if !dataCheckResult.success> style="color: red" </#if>>${dataCheckResult.success?string("是","否")}</td>
            <td>${dataCheckResult.errorCount!}</td>
            <td>${dataCheckResult.remark!}</td>
        </tr>
        </#list>
        </tbody>
    </table>
</div>
</body>
</html>