package com.Restaurante.Dove.service;

import com.Restaurante.Dove.model.CardapioEntity;
import com.Restaurante.Dove.repository.CardapioRepository;
import com.Restaurante.Dove.repository.IngredienteRepository;
import com.Restaurante.Dove.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitarios do CardapioService.
 *
 * Diferente do smoke test (que so confirma que o contexto Spring sobe),
 * este teste valida regras de negocio reais do service usando Mockito
 * para simular o repository, sem precisar de banco de dados nem subir
 * o contexto completo da aplicacao — por isso roda muito mais rapido.
 */
@ExtendWith(MockitoExtension.class)
class CardapioServiceTest {

    @Mock
    private CardapioRepository cardapioRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private IngredienteRepository ingredienteRepository;

    private CardapioService cardapioService;

    @BeforeEach
    void setUp() {
        cardapioService = new CardapioService(
                cardapioRepository,
                pedidoRepository,
                ingredienteRepository
        );
    }

    @Test
    void deveSalvarCardapioQuandoDataNaoExistente() {
        CardapioEntity novoCardapio = new CardapioEntity();
        novoCardapio.setData(LocalDate.of(2026, 8, 10));

        when(cardapioRepository.findAll()).thenReturn(Collections.emptyList());
        when(cardapioRepository.save(novoCardapio)).thenReturn(novoCardapio);

        CardapioEntity salvo = cardapioService.save(novoCardapio);

        assertNotNull(salvo);
        assertEquals(LocalDate.of(2026, 8, 10), salvo.getData());
        verify(cardapioRepository, times(1)).save(novoCardapio);
    }

    @Test
    void naoDeveSalvarCardapioSemData() {
        CardapioEntity cardapioSemData = new CardapioEntity();
        cardapioSemData.setData(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> cardapioService.save(cardapioSemData)
        );

        assertEquals("A data do cardápio é obrigatória", exception.getMessage());
        verify(cardapioRepository, never()).save(any());
    }

    @Test
    void naoDeveSalvarCardapioComDataDuplicada() {
        LocalDate data = LocalDate.of(2026, 8, 10);

        CardapioEntity cardapioExistente = new CardapioEntity();
        cardapioExistente.setData(data);

        CardapioEntity cardapioNovo = new CardapioEntity();
        cardapioNovo.setData(data);

        when(cardapioRepository.findAll()).thenReturn(List.of(cardapioExistente));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> cardapioService.save(cardapioNovo)
        );

        assertTrue(exception.getMessage().contains("Já existe um cardápio cadastrado"));
        verify(cardapioRepository, never()).save(any());
    }

    @Test
    void deveRetornarTodosOsCardapiosCadastrados() {
        CardapioEntity cardapio1 = new CardapioEntity();
        cardapio1.setData(LocalDate.of(2026, 8, 10));

        CardapioEntity cardapio2 = new CardapioEntity();
        cardapio2.setData(LocalDate.of(2026, 8, 11));

        when(cardapioRepository.findAll()).thenReturn(Arrays.asList(cardapio1, cardapio2));

        List<CardapioEntity> resultado = cardapioService.findAll();

        assertEquals(2, resultado.size());
        verify(cardapioRepository, times(1)).findAll();
    }

    @Test
    void deveLancarExcecaoAoBuscarCardapioComIdInexistente() {
        when(cardapioRepository.findById(999)).thenReturn(java.util.Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> cardapioService.findById(999L)
        );

        assertTrue(exception.getMessage().contains("não encontrado"));
    }
}
