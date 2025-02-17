package com.tenniscourts.guests;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Data
public class GuestDTO {

    private Long id;

    @NonNull
    private String name;
}
