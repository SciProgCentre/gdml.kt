package space.kscience.gdml

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.Token
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import kotlin.math.pow

private class ArithmeticsEvaluator(val variables: Map<String, Double>) : Grammar<Double>() {
    val num: Token by regexToken("[\\d.]+(?:[eE][-+]?\\d+)?")
    val id: Token by regexToken("[a-z_A-Z][\\da-z_A-Z]*")
    val lpar by literalToken("(")
    val rpar by literalToken(")")
    val mul by literalToken("*")
    val pow by literalToken("^")
    val div by literalToken("/")
    val minus by literalToken("-")
    val plus by literalToken("+")
    val ws by regexToken("\\s+", ignore = true)

    val number: Parser<Double> by num use { text.toDouble() }
    val symbol: Parser<Double> by id use {  variables[text] ?: error("Variable $text not found") }

    val term: Parser<Double> by number or symbol or
            (skip(plus) and parser(::term)) or
            (skip(minus) and parser(::term) map { -it }) or
            (skip(lpar) and parser(::rootParser) and skip(rpar))

    val powChain by leftAssociative(term, pow) { a, _, b -> a.pow(b) }

    val divMulChain by leftAssociative(powChain, div or mul use { type }) { a, op, b ->
        if (op == div) a / b else a * b
    }

    val subSumChain by leftAssociative(divMulChain, plus or minus use { type }) { a, op, b ->
        if (op == plus) a + b else a - b
    }

    override val rootParser: Parser<Double> by subSumChain
}

public fun Map<String, Double>.parseAndEvaluate(expr: String): Double = ArithmeticsEvaluator(this).parseToEnd(expr)
