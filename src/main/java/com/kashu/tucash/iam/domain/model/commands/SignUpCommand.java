package com.kashu.tucash.iam.domain.model.commands;

public record SignUpCommand(String email, String password, String displayName) {
}
