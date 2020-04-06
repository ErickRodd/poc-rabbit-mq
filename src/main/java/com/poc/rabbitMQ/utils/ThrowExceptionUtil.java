package com.poc.rabbitMQ.utils;

import java.io.IOException;
import java.util.Random;

public class ThrowExceptionUtil {
    public static void randomIOException() throws IOException {
        if (new Random().nextBoolean()) {
            throw new IOException("Erro gerado aleatóriamente para testar o delay do reenvio de mensagens rejeitadas.", new IOException("Erro gerado aleatóriamente para testar o delay do reenvio de mensagens rejeitadas."));
        }
    }
}
