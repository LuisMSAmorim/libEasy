package br.com.amorimtech.libEasy.book.repository;

import br.com.amorimtech.libEasy.book.document.BookDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookSearchRepository extends ElasticsearchRepository<BookDocument, Long> {
    
    /**
     * Busca em múltiplos campos (title, author, description) com relevância otimizada.
     * Usa fuzzy search para tolerância a erros e wildcard para busca parcial.
     * 
     * Boost (relevância):
     * - Title: 3.0 (maior relevância)
     * - Author: 2.0 (média relevância)
     * - Description: 1.0 (menor relevância)
     */
    @Query("""
        {
          "bool": {
            "should": [
              {
                "match": {
                  "title": {
                    "query": "?0",
                    "boost": 3.0,
                    "fuzziness": "AUTO"
                  }
                }
              },
              {
                "match": {
                  "author": {
                    "query": "?0",
                    "boost": 2.0,
                    "fuzziness": "AUTO"
                  }
                }
              },
              {
                "match": {
                  "description": {
                    "query": "?0",
                    "boost": 1.0,
                    "fuzziness": "AUTO"
                  }
                }
              },
              {
                "wildcard": {
                  "title": {
                    "value": "*?0*",
                    "boost": 2.0
                  }
                }
              },
              {
                "wildcard": {
                  "author": {
                    "value": "*?0*",
                    "boost": 1.5
                  }
                }
              },
              {
                "wildcard": {
                  "description": {
                    "value": "*?0*",
                    "boost": 0.5
                  }
                }
              }
            ],
            "minimum_should_match": 1
          }
        }
        """)
    Page<BookDocument> searchByAllFields(String query, Pageable pageable);
}
