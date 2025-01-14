package com.eshop.client.service;

import com.eshop.client.enums.DistanceType;
import com.eshop.client.util.DistanceUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DistanceUtilTest {

    @Test
    public void distance_shoudRetun_2KM() {
        assertThat(DistanceUtil.distance(5.05D, 4.05D, 5.07D, 4.05D, DistanceType.Kilometers)).isEqualTo(0.03D);
        assertThat(DistanceUtil.distance(40.689202777778D, 38.889069444444D, -74.044219444444D, -77.034502777778D, DistanceType.Kilometers)).isEqualTo(218.69D);
    }
}
