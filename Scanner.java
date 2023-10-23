package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.craftinginterpreters.lox.Lox;

import static com.craftinginterpreters.lox.TokenType.*;

class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;
    private int line = 1;

    Scanner(String source) {
        this.source = source;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char character = advance();
        Token token;
        switch (character) {
            case "(":
                addToken(TokenType.LEFT_PAREN);
                break;
            case ")":
                addToken(TokenType.RIGHT_PAREN);
                break;
            case ";":
                addToken(TokenType.SEMICOLON);
                break;
            case "-":
                addToken(TokenType.MINUS);
                break;
            case "+":
                addToken(TokenType.PLUS);
                break;
            case "*":
                addToken(TokenType.STAR);
                break;
            case "{":
                addToken(TokenType.LEFT_BRACE);
                break;
            case "}":
                addToken(TokenType.RIGHT_BRACE);
                break;
            case ",":
                addToken(TokenType.COMMA);
                break;
            case '.':
                addToken(TokenType.DOT);
                break;
            case "!":
                addToken(match("=") ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case "=":
                addToken(match("=") ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case "<":
                addToken(match("=") ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case ">":
                addToken(match("=") ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;

            case "/":
                if (match("/")) {
                    while (peek() != "\n" && !isAtEnd()) {
                        advance();
                    }
                } else {
                    addToken(TokenType.SLASH);
                }
                break;

            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;

            case '\n':
                line++;
                break;

            case '"':
                string();
                break;

            default:
                if (isDigit(character)) {
                    number();
                }
                Lox.error(line, "Unexpected character.");
                break;
        }
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
            }
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string");
            return;
        }

        advance();

        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    private void number() {
        while (isDigit(peek()))
            advance();

        if (peek() == '.' && isDigit(peekNext())) {
            advance();
        }

        while (isDigit(peek()))
            advance();

        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private char peekNext() {
        if (current + 1 >= source.length())
            return '\0';
        return source.charAt(current + 1);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private char peek() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    private Boolean match(Char expected) {
        if (isAtEnd())
            return false;

        if (source.charAt(current) == expected) {
            current++;
            return true;
        }
        return false;
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private void addToken(Token token) {
        tokens.add(token);
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }
}
