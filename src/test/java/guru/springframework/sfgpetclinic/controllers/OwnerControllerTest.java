package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.fauxspring.Model;
import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {
    private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";

    @Mock(lenient = true)
    OwnerService ownerService;

    @Mock
    Model model;

    @InjectMocks
    OwnerController controller;

    @Mock
    BindingResult bindingResult;

    @Captor
    ArgumentCaptor<String> argumentCaptor;

    @BeforeEach
    void setUp() {
        given(ownerService.findAllByLastNameLike(argumentCaptor.capture())).willAnswer(invocation -> {
            List<Owner> owners = new ArrayList<>();

            String name = invocation.getArgument(0);

           if(name.equals("%Wiratama%")) {
               owners.add(new Owner(1L, "Winadi", "Wiratama"));
               return owners;
           } else if(name.equals("%DontFindMe%")) {
               return owners;
           } else if(name.equals("%FindMe%")) {
               owners.add(new Owner(1L, "Win", "Wiratama"));
               owners.add(new Owner(2L, "Winadi", "Wiratama"));
               return owners;
           }

           throw new RuntimeException("Invalid Argument");
        }
        );
    }

    @Test
    void processFindFormWildcardsStringAnnotation() {
        //given
        Owner owner = new Owner(null, "Winadi", "Wiratama");
        List<Owner> ownerList = new ArrayList<>();

        //when
        String viewName = controller.processFindForm(owner, bindingResult, null);

        //then
        assertThat("%Wiratama%").isEqualToIgnoringCase(argumentCaptor.getValue());
        assertThat("redirect:/owners/1").isEqualToIgnoringCase(viewName);
        verifyZeroInteractions(model);
    }

    @Test
    void processFindFormWildcardNotFound() {
        //given
        Owner owner = new Owner(null, "Winadi", "DontFindMe");
        List<Owner> ownerList = new ArrayList<>();

        //when
        String viewName = controller.processFindForm(owner, bindingResult, null);

        //then
        assertThat("%DontFindMe%").isEqualToIgnoringCase(argumentCaptor.getValue());
        assertThat("owners/findOwners").isEqualToIgnoringCase(viewName);
        verifyZeroInteractions(model);
    }

    @Test
    void processFindFormWildcardFound() {
        //given
        Owner owner = new Owner(null, "Winadi", "FindMe");
        InOrder inOrder = Mockito.inOrder(ownerService, model);

        //when
        String viewName = controller.processFindForm(owner, bindingResult, model);

        //then
        assertThat("%FindMe%").isEqualToIgnoringCase(argumentCaptor.getValue());
        assertThat("owners/ownersList").isEqualToIgnoringCase(viewName);

        // inorder assertion
        inOrder.verify(ownerService).findAllByLastNameLike(anyString());
        inOrder.verify(model, times(1)).addAttribute(anyString(), anyList());
        verifyNoMoreInteractions(model);
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