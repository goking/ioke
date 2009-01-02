/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package ioke.lang;

import java.util.ArrayList;
import java.util.HashMap;

import ioke.lang.exceptions.ControlFlow;

/**
 *
 * @author <a href="mailto:ola.bini@gmail.com">Ola Bini</a>
 */
public class DefaultSyntax extends IokeData implements Named, Inspectable, AssociatedCode {
    String name;
    private IokeObject context;
    private IokeObject code;

    public DefaultSyntax(String name) {
        this.name = name;
    }

    public DefaultSyntax(IokeObject context, IokeObject code) {
        this((String)null);

        this.context = context;
        this.code = code;
    }

    public IokeObject getCode() {
        return code;
    }

    public String getCodeString() {
        return "syntax(" + Message.code(code) + ")";

    }

    public String getFormattedCode(Object self) throws ControlFlow {
        return "syntax(\n  " + Message.formattedCode(code, 2) + ")";
    }
    
    @Override
    public void init(IokeObject syntax) throws ControlFlow {
        syntax.setKind("DefaultSyntax");
        syntax.registerCell("activatable", syntax.runtime._true);

        syntax.registerMethod(syntax.runtime.newJavaMethod("returns the name of the syntax", new JavaMethod.WithNoArguments("name") {
                @Override
                public Object activate(IokeObject self, IokeObject context, IokeObject message, Object on) throws ControlFlow {
                    getArguments().getEvaluatedArguments(context, message, on, new ArrayList<Object>(), new HashMap<String, Object>());
                    return context.runtime.newText(((DefaultSyntax)IokeObject.data(on)).name);
                }
            }));
        syntax.registerMethod(syntax.runtime.newJavaMethod("activates this syntax with the arguments given to call", new JavaMethod("call") {
                private final DefaultArgumentsDefinition ARGUMENTS = DefaultArgumentsDefinition
                    .builder()
                    .withRestUnevaluated("arguments")
                    .getArguments();

                @Override
                public DefaultArgumentsDefinition getArguments() {
                    return ARGUMENTS;
                }

                @Override
                public Object activate(IokeObject self, IokeObject context, IokeObject message, Object on) throws ControlFlow {
                    return IokeObject.as(on).activate(context, message, context.getRealContext());
                }
            }));
        syntax.registerMethod(syntax.runtime.newJavaMethod("returns the message chain for this syntax", new JavaMethod.WithNoArguments("message") {
                @Override
                public Object activate(IokeObject self, IokeObject context, IokeObject message, Object on) throws ControlFlow {
                    getArguments().getEvaluatedArguments(context, message, on, new ArrayList<Object>(), new HashMap<String, Object>());
                    return ((AssociatedCode)IokeObject.data(on)).getCode();
                }
            }));
        syntax.registerMethod(syntax.runtime.newJavaMethod("returns the code for the argument definition", new JavaMethod.WithNoArguments("argumentsCode") {
                @Override
                public Object activate(IokeObject self, IokeObject dynamicContext, IokeObject message, Object on) throws ControlFlow {
                    getArguments().getEvaluatedArguments(dynamicContext, message, on, new ArrayList<Object>(), new HashMap<String, Object>());
                    return dynamicContext.runtime.newText(((AssociatedCode)IokeObject.data(on)).getArgumentsCode());
                }
            }));
        syntax.registerMethod(syntax.runtime.newJavaMethod("Returns a text inspection of the object", new JavaMethod.WithNoArguments("inspect") {
                @Override
                public Object activate(IokeObject self, IokeObject context, IokeObject message, Object on) throws ControlFlow {
                    getArguments().getEvaluatedArguments(context, message, on, new ArrayList<Object>(), new HashMap<String, Object>());
                    return context.runtime.newText(DefaultSyntax.getInspect(on));
                }
            }));
        syntax.registerMethod(syntax.runtime.newJavaMethod("Returns a brief text inspection of the object", new JavaMethod.WithNoArguments("notice") {
                @Override
                public Object activate(IokeObject self, IokeObject context, IokeObject message, Object on) throws ControlFlow {
                    getArguments().getEvaluatedArguments(context, message, on, new ArrayList<Object>(), new HashMap<String, Object>());
                    return context.runtime.newText(DefaultSyntax.getNotice(on));
                }
            }));
        syntax.registerMethod(syntax.runtime.newJavaMethod("returns the full code of this syntax, as a Text", new JavaMethod.WithNoArguments("code") {
                @Override
                public Object activate(IokeObject self, IokeObject context, IokeObject message, Object on) throws ControlFlow {
                    getArguments().getEvaluatedArguments(context, message, on, new ArrayList<Object>(), new HashMap<String, Object>());
                    IokeData data = IokeObject.data(on);
                    if(data instanceof DefaultSyntax) {
                        return context.runtime.newText(((DefaultSyntax)data).getCodeString());
                    } else {
                        return context.runtime.newText(((AliasMethod)data).getCodeString());
                    }
                }
            }));
        syntax.registerMethod(syntax.runtime.newJavaMethod("returns idiomatically formatted code for this syntax", new JavaMethod.WithNoArguments("formattedCode") {
                @Override
                public Object activate(IokeObject self, IokeObject context, IokeObject message, Object on) throws ControlFlow {
                    getArguments().getEvaluatedArguments(context, message, on, new ArrayList<Object>(), new HashMap<String, Object>());
                    return context.runtime.newText(((AssociatedCode)IokeObject.data(on)).getFormattedCode(self));
                }
            }));
    }

    public String getArgumentsCode() {
        return "...";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getInspect(Object on) {
        return ((Inspectable)(IokeObject.data(on))).inspect(on);
    }

    public static String getNotice(Object on) {
        return ((Inspectable)(IokeObject.data(on))).notice(on);
    }

    public String inspect(Object self) {
        if(name == null) {
            return "syntax(" + Message.code(code) + ")";
        } else {
            return name + ":syntax(" + Message.code(code) + ")";
        }
    }

    public String notice(Object self) {
        if(name == null) {
            return "syntax(...)";
        } else {
            return name + ":syntax(...)";
        }
    }

    @Override
    public Object activate(IokeObject self, IokeObject context, IokeObject message, Object on) throws ControlFlow {
        if(code == null) {
            IokeObject condition = IokeObject.as(IokeObject.getCellChain(context.runtime.condition, 
                                                                         message, 
                                                                         context, 
                                                                         "Error", 
                                                                         "Invocation",
                                                                         "NotActivatable")).mimic(message, context);
            condition.setCell("message", message);
            condition.setCell("context", context);
            condition.setCell("receiver", on);
            condition.setCell("method", self);
            condition.setCell("report", context.runtime.newText("You tried to activate a method without any code - did you by any chance activate the DefaultSyntax kind by referring to it without wrapping it inside a call to cell?"));
            context.runtime.errorCondition(condition);
            return null;
        }

        IokeObject c = context.runtime.locals.mimic(message, context);
        c.setCell("self", on);
        c.setCell("@", on);
        c.setCell("currentMessage", message);
        c.setCell("surroundingContext", context);
        c.setCell("call", context.runtime.newCallFrom(c, message, context, IokeObject.as(on)));

        Object result = null;

        try {
            result = code.evaluateCompleteWith(c, on);
        } catch(ControlFlow.Return e) {
            if(e.context == c) {
                result = e.getValue();
            } else {
                throw e;
            }
        }

        if(result == context.runtime.nil) {
            // Remove chain completely
            IokeObject prev = Message.prev(message);
            IokeObject next = Message.next(message);
            if(prev != null) {
                Message.setNext(prev, next);
                if(next != null) {
                    Message.setPrev(next, prev);
                }
            } else {
                message.become(next, message, context);
                Message.setPrev(next, null);
            }
            return null;
        } else {
            // Insert resulting value into chain, wrapping it if it's not a message

            IokeObject newObj = null;
            if(IokeObject.data(result) instanceof Message) {
                newObj = IokeObject.as(result);
            } else {
                newObj = context.runtime.createMessage(Message.wrap(IokeObject.as(result)));
            }

            IokeObject prev = Message.prev(message);
            IokeObject next = Message.next(message);

            message.become(newObj, message, context);

            IokeObject last = newObj;
            while(Message.next(last) != null) {
                last = Message.next(last);
            }
            Message.setNext(last, next);
            if(next != null) {
                Message.setPrev(next, last);
            }
            Message.setPrev(newObj, prev);

            return message.sendTo(context, context);
        }
    }
}// DefaultSyntax
