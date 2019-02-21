package com.github.artsiomch.commentfolder;

import com.intellij.codeInsight.folding.JavaCodeFoldingSettings;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.lang.folding.NamedFoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiComment;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockCommentFoldingBuilder implements FoldingBuilder {

  @NotNull
  @Override
  public FoldingDescriptor[] buildFoldRegions(@NotNull ASTNode node, @NotNull Document document) {
    return PsiTreeUtil.findChildrenOfType(node.getPsi(), PsiComment.class).stream()
        .filter(it -> it.getTokenType() == JavaTokenType.C_STYLE_COMMENT)
        .filter(it -> isMultiLine(document, it))
        .map(this::createFoldingDescriptor)
        .toArray(FoldingDescriptor[]::new);
  }

  @NotNull
  private NamedFoldingDescriptor createFoldingDescriptor(PsiComment comment) {
    String text = comment.getText();
    if (text.length() > 200) text = text.substring(0, 200);
    text = text.replaceAll(" {2,}", " ");
    String placeholderText = (text.length() < 60) ? text : text.substring(0, 50) + " ... */";
    return new NamedFoldingDescriptor(
        comment.getNode(),
        comment.getTextRange(),
  null, // FoldingGroup.newGroup("Block comment " + comment.getTextRange().toString()),
        placeholderText
/*
        ,
        JavaCodeFoldingSettings.getInstance().isCollapseEndOfLineComments(),
        Collections.emptySet()
*/
    );
  }

  private boolean isMultiLine(@NotNull Document document, @NotNull PsiComment psiComment) {
    final TextRange range = psiComment.getTextRange();
    final int firstLineNumber = document.getLineNumber(range.getStartOffset());
    final int lastLineNumber = document.getLineNumber(range.getEndOffset());
    return firstLineNumber != lastLineNumber;
  }

  @Nullable
  @Override
  public String getPlaceholderText(@NotNull ASTNode node) {
    return null;
  }

  @Override
  public boolean isCollapsedByDefault(@NotNull ASTNode node) {
    return false;
  }
}
