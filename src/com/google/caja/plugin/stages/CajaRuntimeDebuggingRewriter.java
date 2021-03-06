// Copyright (C) 2008 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.caja.plugin.stages;

import com.google.caja.lexer.FilePosition;
import com.google.caja.lexer.InputSource;
import com.google.caja.parser.ParseTreeNode;
import com.google.caja.parser.js.IntegerLiteral;
import com.google.caja.parser.js.SyntheticNodes;
import com.google.caja.parser.quasiliteral.QuasiBuilder;
import com.google.caja.parser.quasiliteral.Rewriter;
import com.google.caja.parser.quasiliteral.Rule;
import com.google.caja.parser.quasiliteral.RuleDescription;
import com.google.caja.parser.quasiliteral.Scope;
import com.google.caja.reporting.MessageQueue;

import java.util.Map;

/**
 * Modifies cajoled code to remove fasttrack branches, and add debugging info
 * to the slow branches.
 *
 * @author mikesamuel@gmail.com
 */
final class CajaRuntimeDebuggingRewriter extends Rewriter {
  private final DebuggingSymbols symbols;

  CajaRuntimeDebuggingRewriter(DebuggingSymbols symbols, MessageQueue mq) {
    super(mq, false, false);
    this.symbols = symbols;
  }

  /**
   * Rewrites a {@code ___} method call to add an extra parameter which is an
   * index into {@link DebuggingSymbols}.
   * The position is drawn from the <code>@posNode</code> binding if one is
   * present, or is otherwise drawn from the match as a whole.
   */
  abstract class AddPositionParamRule extends Rule {
    // TODO(erights): Nothing is gained by having this be a partial override of
    // fire() which subclasses have to further override. Make into a protected
    // (or package scoped) method instead, where the concrete fire() methods
    // simply call it. Or perhaps have it override transform().
    @Override
    public ParseTreeNode fire(ParseTreeNode node, Scope scope) {
      Map<String, ParseTreeNode> bindings = match(node);
      if (bindings != null) {
        Map<String, ParseTreeNode> newBindings = makeBindings();
        for (Map.Entry<String, ParseTreeNode> entry : bindings.entrySet()) {
          newBindings.put(entry.getKey(), expand(entry.getValue(), scope));
        }

        ParseTreeNode posNode = bindings.get("posNode");
        if (posNode == null) { posNode = node; }
        FilePosition pos = spanningPos(posNode);
        if (pos == null) { pos = FilePosition.UNKNOWN; }
        int index = symbols.indexForPosition(pos);
        newBindings.put("debug",
                        new IntegerLiteral(FilePosition.UNKNOWN, index));
        return QuasiBuilder.subst(getRuleDescription().substitutes(),
                                  newBindings);
      }
      return NONE;
    }
  }

  {
    addRule(new AddPositionParamRule() {
          @Override
          @RuleDescription(
              name="callPub",
              synopsis="adds debug info to ___.callPub calls",
              reason="",
              matches="___.callPub(@obj, @name, @args)",
              substitutes="___.callPub(@obj, @name, @args, @debug)")
          public ParseTreeNode fire(ParseTreeNode n, Scope s) {
            return super.fire(n, s);
          }
        });
    addRule(new AddPositionParamRule() {
          @Override
          @RuleDescription(
              name="deletePub",
              synopsis="adds debug info to ___.deletePub calls",
              reason="",
              matches="___.deletePub(@obj, @name)",
              substitutes="___.deletePub(@obj, @name, @debug)")
          public ParseTreeNode fire(ParseTreeNode n, Scope s) {
            return super.fire(n, s);
          }
        });
    addRule(new AddPositionParamRule() {
          @Override
          @RuleDescription(
              name="readPub",
              synopsis="adds debug info to ___.readPub calls",
              reason="",
              matches="___.readPub(@obj, @name)",
              substitutes="___.readPub(@obj, @name, @debug)")
          public ParseTreeNode fire(ParseTreeNode n, Scope s) {
            return super.fire(n, s);
          }
        });
    addRule(new AddPositionParamRule() {
          @Override
          @RuleDescription(
              name="inPub",
              synopsis="adds debug info to ___.inPub calls",
              reason="",
              matches="___.inPub(@obj, @name)",
              substitutes="___.inPub(@obj, @name, @debug)")
          public ParseTreeNode fire(ParseTreeNode n, Scope s) {
            return super.fire(n, s);
          }
        });
    addRule(new AddPositionParamRule() {
          @Override
          @RuleDescription(
              name="setPub",
              synopsis="adds debug info to ___.setPub calls",
              reason="",
              matches="___.setPub(@obj, @name, @val)",
              substitutes="___.setPub(@obj, @name, @val, @debug)")
          public ParseTreeNode fire(ParseTreeNode n, Scope s) {
            return super.fire(n, s);
          }
        });
    addRule(new AddPositionParamRule() {
          @Override
          @RuleDescription(
              name="asFunc",
              synopsis="Turns .CALL___ calls into ___.asFunc calls with debugging info",
              reason="",
              matches="@fun.CALL___",
              substitutes="___.asFunc(@fun, @debug)")
          public ParseTreeNode fire(ParseTreeNode n, Scope s) {
            return super.fire(n, s);
          }
        });
    addRule(new AddPositionParamRule() {
          @Override
          @RuleDescription(
              name="frozenFuncAnon",
              synopsis="adds debug info to ___.markFuncFreeze calls",
              reason="",
              matches="___.markFuncFreeze(@fun)",
              substitutes="___.markFuncFreeze(@fun, undefined, @debug)")
          public ParseTreeNode fire(ParseTreeNode n, Scope s) {
            return super.fire(n, s);
          }
        });
    addRule(new AddPositionParamRule() {
          @Override
          @RuleDescription(
              name="frozenFuncNamed",
              synopsis="adds debug info to ___.markFuncFreeze calls",
              reason="",
              matches="___.markFuncFreeze(@fun, @name)",
              substitutes="___.markFuncFreeze(@fun, @name, @debug)")
          public ParseTreeNode fire(ParseTreeNode n, Scope s) {
            return super.fire(n, s);
          }
        });
    addRule(new AddPositionParamRule() {
      @Override
      @RuleDescription(
          name="construct",
          synopsis="adds debug info to ___.construct calls",
          reason="",
          matches="___.construct(@fun, @args)",
          substitutes="___.construct(@fun, @args, @debug)")
      public ParseTreeNode fire(ParseTreeNode n, Scope s) {
        return super.fire(n, s);
      }
    });
    addRule(new Rule() {
          @Override
          @RuleDescription(
              name="simplifyCall",
              synopsis="remove fasttrack so it can't dereference null",
              reason=("fasttrack useless in debug mode but it can still cause"
                      + "errors outside our stack"),
              matches="@obj.@x_canCall___ ? (@obj.@x(@actuals*)) : @operation",
              substitutes="@operation")
          public ParseTreeNode fire(ParseTreeNode n, Scope s) {
            return transform(n, s);
          }
        });
    addRule(new Rule() {
          @Override
          @RuleDescription(
              name="simplifyRead",
              synopsis="remove fasttrack so it can't dereference null",
              reason=("fasttrack useless in debug mode but it can still cause"
                      + "errors outside our stack"),
              matches="@obj.@key_canRead___ ? (@obj.@key) : @operation",
              substitutes="@operation")
          public ParseTreeNode fire(ParseTreeNode n, Scope s) {
            return transform(n, s);
          }
        });
    addRule(new Rule() {
          @Override
          @RuleDescription(
              name="simplifySet",
              synopsis="remove fasttrack so it can't dereference null",
              reason=("fasttrack useless in debug mode but it can still cause"
                      + "errors outside our stack"),
              matches="@obj.@key_canSet___ === @obj ? (@obj.@key = @y) : @operation",
              substitutes="@operation")
          public ParseTreeNode fire(ParseTreeNode n, Scope s) {
            return transform(n, s);
          }
        });
    addRule(new AddPositionParamRule() {
          @Override
          @RuleDescription(
              name="inOperator",
              synopsis="Add null check to rhs of 'in' operator",
              reason="to identify the source of null pointer exceptions",
              matches=("(@key in @posNode)"),
              substitutes=("(@key in ___.requireObject(@posNode, @debug))"))
          public ParseTreeNode fire(ParseTreeNode n, Scope s) {
            return super.fire(n, s);
          }
        });
    addRule(new AddPositionParamRule() {
          @Override
          @RuleDescription(
              name="userException",
              synopsis="Add null check to rhs of 'in' operator",
              reason="identify the location where user errors are thrown",
              matches="throw @ex",
              substitutes="throw ___.userException(@ex, @debug)")
          public ParseTreeNode fire(ParseTreeNode n, Scope s) {
            return super.fire(n, s);
          }
        });
    addRule(new Rule() {
          @Override
          @RuleDescription(
              name="fallback",
              synopsis="handles cases not recognized by ",
              reason="because not everything is a runtime check")
          public ParseTreeNode fire(ParseTreeNode n, Scope s) {
            return expandAll(n, s);
          }
        });
  }

  private FilePosition spanningPos(ParseTreeNode node) {
    FilePosition pos = node.getFilePosition();
    if (!node.getAttributes().is(SyntheticNodes.SYNTHETIC)
        && !InputSource.UNKNOWN.equals(pos.source())) {
      return pos;
    }

    FilePosition start = null, end = null;
    for (ParseTreeNode child : node.children()) {
      FilePosition childPos = spanningPos(child);
      if (childPos != null) {
        if (start == null) {
          start = end = childPos;
        } else if (start.source().equals(childPos.source())) {
          if (childPos.startCharInFile() < start.startCharInFile()) {
            start = childPos;
          }
          assert end != null;  // end == null implies start == null above
          if (childPos.endCharInFile() > end.endCharInFile()) {
            end = childPos;
          }
        }
      }
    }
    if (start != null) { return FilePosition.span(start, end); }
    return null;
  }
}
