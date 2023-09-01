package com.github.le_yams.openfga4intellij.parsing;

import com.github.le_yams.openfga4intellij.psi.OpenFGATypes;
import com.intellij.psi.tree.TokenSet;

public interface OpenFGATokenSets {

    TokenSet KEYWORDS = TokenSet.create(
            OpenFGATypes.MODEL,
            OpenFGATypes.SCHEMA,
            OpenFGATypes.TYPE,
            OpenFGATypes.RELATIONS,
            OpenFGATypes.DEFINE,
            OpenFGATypes.OR,
            OpenFGATypes.FROM
    );

    TokenSet RELATION_NAME = TokenSet.create(OpenFGATypes.RELATION_NAME);
    TokenSet TYPE_IDENTIFIER = TokenSet.create(OpenFGATypes.TYPE_IDENTIFIER);
    TokenSet SINGLE_LINE_COMMENT = TokenSet.create(OpenFGATypes.COMMENT);
//    TokenSet DUMMY_WHITE_SPACE = TokenSet.create(OpenFGATypes.DUMMY_WHITE_SPACE);
}
