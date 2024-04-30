package io.github.xiaozhuai;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.lang.reflect.Method;

public abstract class MyStringLiteralHandler {
    private final String className;
    private final String pluginId;
    private final IdeaPluginDescriptor plugin;
    private final ClassLoader classLoader;
    private final Class<PsiElement> clazz;

    @SuppressWarnings("unchecked")
    public MyStringLiteralHandler(String className, String pluginId) {
        this.className = className;
        this.pluginId = pluginId;
        this.plugin = pluginId.isEmpty() ? null : PluginManagerCore.getPlugin(PluginId.getId(pluginId));
        this.classLoader = plugin == null ? getClass().getClassLoader() : plugin.getPluginClassLoader();
        Class<PsiElement> clazz = null;
        try {
            clazz = (Class<PsiElement>) Class.forName(className, true, classLoader);
        } catch (ClassNotFoundException e) {
            System.out.println("!!! Class not found: " + className);
        }
        this.clazz = clazz;
    }

    public final String getClassName() {
        return className;
    }

    public final String getPluginId() {
        return pluginId;
    }

    public final IdeaPluginDescriptor getPlugin() {
        return plugin;
    }

    public final ClassLoader getClassLoader() {
        return classLoader;
    }

    public final Class<PsiElement> getClazz() {
        return clazz;
    }

    public final String getFinalValue(PsiElement psiElement) {
        String text = getValue(psiElement);
        text = removeScheme(text);
        return text;
    }

    protected abstract String getValue(PsiElement psiElement);

    private static String removeScheme(String text) {
        if (text.startsWith("qrc:///")) {
            text = text.substring(7);
        } else if (text.contains("://")) {
            text = text.substring(text.indexOf("://") + 3);
        } else if (text.startsWith(":/")) {
            text = text.substring(2);
        }
        return text;
    }

    private static MyStringLiteralHandler[] handlers = null;

    public static MyStringLiteralHandler[] getAllHandlers() {
        if (handlers == null) {
            handlers = new MyStringLiteralHandler[]{
                    // Java & groovy
                    new MyStringLiteralHandler("com.intellij.psi.PsiLiteral", "com.intellij.java") {
                        @Override
                        protected String getValue(PsiElement psiElement) {
                            try {
                                Method getValue = this.getClazz().getMethod("getValue");
                                return (String) getValue.invoke(psiElement);
                            } catch (Exception e) {
                                System.out.println("!!! Error GetValue: " + e.getMessage());
                                return "";
                            }
                        }
                    },

                    // kotlin
                    new MyStringLiteralHandler("org.jetbrains.kotlin.psi.KtStringTemplateExpression", "org.jetbrains.kotlin") {
                        @Override
                        protected String getValue(PsiElement psiElement) {
                            try {
                                Class<?> escaperClazz = Class.forName("org.jetbrains.kotlin.psi.KotlinStringLiteralTextEscaper", true, this.getClassLoader());
                                Method createLiteralTextEscaper = this.getClazz().getMethod("createLiteralTextEscaper");
                                Object escaper = createLiteralTextEscaper.invoke(psiElement);
                                Method getRelevantTextRange = escaperClazz.getMethod("getRelevantTextRange");
                                Method decode = escaperClazz.getMethod("decode", TextRange.class, StringBuilder.class);
                                TextRange range = (TextRange) getRelevantTextRange.invoke(escaper);
                                StringBuilder builder = new StringBuilder();
                                decode.invoke(escaper, range, builder);
                                return builder.toString();
                            } catch (Exception e) {
                                System.out.println("!!! Error GetValue: " + e.getMessage());
                                return "";
                            }
                        }
                    },

                    // C/C++
                    // new MyStringLiteralHandler("com.jetbrains.cidr.lang.psi.OCLiteralExpression", "com.jetbrains.cidr.lang") {
                    //     @Override
                    //     protected String getValue(PsiElement psiElement) {
                    //         try {
                    //             Method getValue = this.getClazz().getMethod("getValue");
                    //             return (String) getValue.invoke(psiElement);
                    //         } catch (Exception e) {
                    //             System.out.println("!!! Error GetValue: " + e.getMessage());
                    //             return "";
                    //         }
                    //     }
                    // },
                    new MyStringLiteralHandler("com.jetbrains.cidr.lang.psi.OCLiteralExpression", "com.intellij.modules.cidr.lang") {
                        @Override
                        protected String getValue(PsiElement psiElement) {
                            try {
                                Method getUnescapedLiteralText = this.getClazz().getMethod("getUnescapedLiteralText");
                                return (String) getUnescapedLiteralText.invoke(psiElement);
                            } catch (Exception e) {
                                System.out.println("!!! Error GetValue: " + e.getMessage());
                                return "";
                            }
                        }
                    },

                    // JavaScript
                    new MyStringLiteralHandler("com.intellij.lang.javascript.psi.JSLiteralExpression", "JavaScript") {
                        @Override
                        protected String getValue(PsiElement psiElement) {
                            try {
                                Method getValue = this.getClazz().getMethod("getValue");
                                return (String) getValue.invoke(psiElement);
                            } catch (Exception e) {
                                System.out.println("!!! Error GetValue: " + e.getMessage());
                                return "";
                            }
                        }
                    },

                    // PHP
                    new MyStringLiteralHandler("com.jetbrains.php.lang.psi.elements.StringLiteralExpression", "com.jetbrains.php") {
                        @Override
                        protected String getValue(PsiElement psiElement) {
                            try {
                                Method getContents = this.getClazz().getMethod("getContents");
                                return (String) getContents.invoke(psiElement);
                            } catch (Exception e) {
                                System.out.println("!!! Error GetValue: " + e.getMessage());
                                return "";
                            }
                        }
                    },

                    // Python
                    // new MyStringLiteralHandler("com.jetbrains.python.psi.StringLiteralExpression", "Pythonid") {
                    //     @Override
                    //     protected String getValue(PsiElement psiElement) {
                    //         try {
                    //             Method getStringValue = this.getClazz().getMethod("getStringValue");
                    //             return (String) getStringValue.invoke(psiElement);
                    //         } catch (Exception e) {
                    //             System.out.println("!!! Error GetValue: " + e.getMessage());
                    //             return "";
                    //         }
                    //     }
                    // },
                    // TODO https://youtrack.jetbrains.com/issue/PY-70729/
                    // new MyStringLiteralHandler("com.jetbrains.python.psi.StringLiteralExpression", "com.intellij.modules.python") {
                    //     @Override
                    //     protected String getValue(PsiElement psiElement) {
                    //         try {
                    //             Method getStringValue = this.getClazz().getMethod("getStringValue");
                    //             return (String) getStringValue.invoke(psiElement);
                    //         } catch (Exception e) {
                    //             System.out.println("!!! Error GetValue: " + e.getMessage());
                    //             return "";
                    //         }
                    //     }
                    // },

                    // Dart
                    new MyStringLiteralHandler("com.jetbrains.lang.dart.psi.DartStringLiteralExpression", "Dart") {
                        @Override
                        protected String getValue(PsiElement psiElement) {
                            try {
                                String text = psiElement.getText();
                                if ((text.startsWith("\"") && text.endsWith("\"")) || (text.startsWith("'") && text.endsWith("'"))) {
                                    return text.substring(1, text.length() - 1);
                                }
                                return "";
                            } catch (Exception e) {
                                System.out.println("!!! Error GetValue: " + e.getMessage());
                                return "";
                            }
                        }
                    },

                    // Go
                    new MyStringLiteralHandler("com.goide.psi.GoStringLiteral", "org.jetbrains.plugins.go") {
                        @Override
                        protected String getValue(PsiElement psiElement) {
                            try {
                                Method getDecodedText = this.getClazz().getMethod("getDecodedText");
                                return (String) getDecodedText.invoke(psiElement);
                            } catch (Exception e) {
                                System.out.println("!!! Error GetValue: " + e.getMessage());
                                return "";
                            }
                        }
                    },

                    // Clojure via Cursive
                    new MyStringLiteralHandler("cursive.psi.impl.ClStringLiteral", "com.cursiveclojure.cursive") {
                        @Override
                        protected String getValue(PsiElement psiElement) {
                            try {
                                Method getValue = this.getClazz().getMethod("getValue");
                                return (String) getValue.invoke(psiElement);
                            } catch (Exception e) {
                                System.out.println("!!! Error GetValue: " + e.getMessage());
                                return "";
                            }
                        }
                    },

                    // Ruby
                    new MyStringLiteralHandler("org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.stringLiterals.RStringLiteralBase", "org.jetbrains.plugins.ruby") {
                        @Override
                        protected String getValue(PsiElement psiElement) {
                            try {
                                Method getContent = this.getClazz().getMethod("getContent");
                                return (String) getContent.invoke(psiElement);
                            } catch (Exception e) {
                                System.out.println("!!! Error GetValue: " + e.getMessage());
                                return "";
                            }
                        }
                    },

                    // Rust
                    new MyStringLiteralHandler("org.rust.lang.core.psi.RsLitExpr", "com.jetbrains.rust") {
                        @Override
                        protected String getValue(PsiElement psiElement) {
                            try {
                                String text = psiElement.getText();
                                if (text.startsWith("\"") && text.endsWith("\"")) {
                                    return text.substring(1, text.length() - 1);
                                }
                                return "";
                            } catch (Exception e) {
                                System.out.println("!!! Error GetValue: " + e.getMessage());
                                return "";
                            }
                        }
                    },

                    // TODO
                    // Swift
                    // C#
                    // Perl
            };
        }
        return handlers;
    }
}
