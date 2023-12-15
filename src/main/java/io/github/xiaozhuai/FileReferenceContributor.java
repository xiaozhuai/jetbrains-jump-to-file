package io.github.xiaozhuai;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class FileReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        // Java
        registerReferenceProvider(registrar, "com.intellij.psi.PsiLiteral", "com.intellij.java");

        // C/C++
        // registerReferenceProvider(registrar, "com.jetbrains.cidr.lang.psi.OCLiteralExpression", "com.jetbrains.cidr.lang", new OneWayPsiFileReference.Options() {{
        //     removeCppHereDoc = true;
        // }});
        registerReferenceProvider(registrar, "com.jetbrains.cidr.lang.psi.OCLiteralExpression", "com.intellij.modules.cidr.lang", new OneWayPsiFileReference.Options() {{
            removeCppHereDoc = true;
        }});

        // JavaScript
        registerReferenceProvider(registrar, "com.intellij.lang.javascript.psi.JSLiteralExpression", "JavaScript", new OneWayPsiFileReference.Options() {{
            removeSingleQuotes = true;
            removeBacktick = true;
        }});

        // PHP
        registerReferenceProvider(registrar, "com.jetbrains.php.lang.psi.elements.StringLiteralExpression", "com.jetbrains.php", new OneWayPsiFileReference.Options() {{
            removeSingleQuotes = true;
        }});

        // Python
        // registerReferenceProvider(registrar, "com.jetbrains.python.psi.StringLiteralExpression", "Pythonid", new OneWayPsiFileReference.Options() {{
        //     removeTripleSingleQuotes = true;
        //     removeSingleQuotes = true;
        // }});
        registerReferenceProvider(registrar, "com.jetbrains.python.psi.StringLiteralExpression", "com.intellij.modules.python", new OneWayPsiFileReference.Options() {{
            removeTripleSingleQuotes = true;
            removeSingleQuotes = true;
        }});

        // Dart
        registerReferenceProvider(registrar, "com.jetbrains.lang.dart.psi.DartStringLiteralExpression", "Dart", new OneWayPsiFileReference.Options() {{
            removeSingleQuotes = true;
        }});

        // Rust
        // registerReferenceProvider(registrar, "org.rust.lang.core.psi.ext.RsLitExpr", "com.jetbrains.rust");

        // swift
        // registerReferenceProvider(registrar, "com.jetbrains.swift.psi.impl.SwiftLiteralExpressionGenImpl", "com.intellij.swift");

        // golang
        // registerReferenceProvider(registrar, "com.goide.psi.GoStringLiteral", "org.jetbrains.plugins.go");

        // c#
        // kotlin
        // groovy
        // ruby
        // perl
    }

    @SuppressWarnings("unchecked")
    private void registerReferenceProvider(PsiReferenceRegistrar registrar, String className, String pluginId, OneWayPsiFileReference.Options options) {
        try {
            IdeaPluginDescriptor plugin = pluginId.isEmpty() ? null : PluginManagerCore.getPlugin(PluginId.getId(pluginId));
            ClassLoader classLoader = plugin == null ? getClass().getClassLoader() : plugin.getPluginClassLoader();
            Class<PsiElement> clazz = (Class<PsiElement>) Class.forName(className, true, classLoader);
            registrar.registerReferenceProvider(StandardPatterns.instanceOf(clazz), new PsiReferenceProvider() {
                @Override
                public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                    return new PsiReference[]{new OneWayPsiFileReference(element, options)};
                }
            });
        } catch (ClassNotFoundException e) {
            // ignore
            System.out.println("!!! Class not found: " + className);
        }
    }

    private void registerReferenceProvider(PsiReferenceRegistrar registrar, String className, String pluginId) {
        registerReferenceProvider(registrar, className, pluginId, new OneWayPsiFileReference.Options());
    }
}
