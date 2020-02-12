package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {
    private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";

    @Mock
    OwnerService ownerService;

    @InjectMocks
    OwnerController controller;

    @Mock
    BindingResult bindingResult;

    @Captor
    ArgumentCaptor<String> argumentCaptor;

    @Test
    void processFindFormWildcardsString() {
        //given
        Owner owner = new Owner(null, "Winadi", "Wiratama");
        List<Owner> ownerList = new ArrayList<>();
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        given(ownerService.findAllByLastNameLike(captor.capture())).willReturn(ownerList);

        //when
        String viewName = controller.processFindForm(owner, bindingResult, null);

        //then
        assertThat("%Wiratama%").isEqualToIgnoringCase(captor.getValue());
    }

    @Test
    void processFindFormWildcardsStringAnnotation() {
        //given
        Owner owner = new Owner(null, "Winadi", "Wiratama");
        List<Owner> ownerList = new ArrayList<>();

        given(ownerService.findAllByLastNameLike(argumentCaptor.capture())).willReturn(ownerList);

        //when
        String viewName = controller.processFindForm(owner, bindingResult, null);

        //then
        assertThat("%Wiratama%").isEqualToIgnoringCase(argumentCaptor.getValue());
    }

    @Test
    void processCreationFormHasErrors() {
        //given
        Owner owner = new Owner(null, "Winadi", "Wiratama");

        Mockito.when(bindingResult.hasErrors()).thenReturn(true);

        //when
        String redirect = controller.processCreationForm(owner, bindingResult);

        //then
        assertEquals(VIEWS_OWNER_CREATE_OR_UPDATE_FORM, redirect);
    }

    @Test
    void processCreationFormNoErrors() {
        //given
        Owner owner = new Owner(null, "Winadi", "Wiratama");
        Owner savedOwner = new Owner(1L, "Winadi", "Wiratama");

        Mockito.when(ownerService.save(owner)).thenReturn(savedOwner);

        //when
        String redirect = controller.processCreationForm(owner, bindingResult);

        //then
        assertEquals("redirect:/owners/1", redirect);
    }
}