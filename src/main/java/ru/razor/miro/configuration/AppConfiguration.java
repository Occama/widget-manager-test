package ru.razor.miro.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.razor.miro.entities.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
public class AppConfiguration {
    @Bean
    @Primary
    public List<Widget> getArrayListBean() {
        return Collections.synchronizedList(new ArrayList<>());
    }
}
