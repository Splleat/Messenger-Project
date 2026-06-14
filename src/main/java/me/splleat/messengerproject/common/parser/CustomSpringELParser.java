package me.splleat.messengerproject.common.parser;

import lombok.RequiredArgsConstructor;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomSpringELParser {
    private static final ExpressionParser PARSER = new SpelExpressionParser();
    private static final ParserContext TEMPLATE = new TemplateParserContext();

    public Object getDynamicValue(String[] parameterNames, Object[] args, String key) {
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        return PARSER.parseExpression(key, TEMPLATE).getValue(context);
    }
}
