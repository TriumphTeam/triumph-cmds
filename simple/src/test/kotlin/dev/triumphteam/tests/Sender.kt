package dev.triumphteam.tests

import dev.triumphteam.cmd.core.message.MessageRegistry
import dev.triumphteam.cmd.core.sender.SenderMapper
import dev.triumphteam.cmd.core.sender.SenderValidator
import dev.triumphteam.cmd.core.subcommand.OldSubCommand

class TestSender

class TestSenderMapper : SenderMapper<TestSender, TestSender> {

    override fun map(defaultSender: TestSender): TestSender = defaultSender
}

class TestSenderValidator : SenderValidator<TestSender> {

    override fun getAllowedSenders(): Set<Class<out TestSender>> = setOf(TestSender::class.java)

    override fun validate(
        messageRegistry: MessageRegistry<TestSender>,
        subCommand: OldSubCommand<TestSender>,
        sender: TestSender,
    ): Boolean = true
}
